#!/usr/bin/python3

import argparse
import os
import subprocess
from colors import print_error, print_success, print_header, print_info, print_warning
from check_deps import check_docker, check_docker_compose, check_docker_group
import check_installs as ch_in
from defold_bob import download_bob

parser = argparse.ArgumentParser('Table games initiator')
parser.add_argument('-f', '--force', action='store_true')
args = parser.parse_args()


def get_project_dir_name() -> str:
    return os.path.dirname(os.path.abspath(__file__)).split('/')[-3]


def does_docker_image_sbt_xrandr_exists() -> bool:
    result = subprocess.run(["docker", "images", "sbt_xrandr", "--format", "{{.Repository}}"], capture_output=True)
    return result.stdout.decode('utf8').startswith('sbt_xrandr')


def start():
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    is_error = False
    if not check_docker():
        print_error('Docker is not installed')
        exit(1)
    if not check_docker_compose():
        print_error('Docker compose is not installed')
        exit(1)
    if not check_docker_group():
        print_error('You are not a part of the docker user group. Join the group by running \
        "usermod -a -G docker <username>" and then re-login.')
        exit(1)
    if not os.path.exists('./.cache'):
        os.mkdir('./.cache')

    proj_root = '/'.join(os.path.dirname(os.path.abspath(__file__)).split('/')[:-2])

    def delete_dir(dir_):
        subprocess.run(['docker', 'run', '--rm',
                        '-v', proj_root + '/web-server:/web-server',
                        '-w', '/web-server',
                        'node:18',
                        'rm', '-rf', dir_])

    if not ch_in.is_web_sever_deps_installed() or args.force:
        print_header('Installing web-server dependencies...')
        if args.force:
            delete_dir('/web-server/node_modules/')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/web-server:/web-server',
                                 '-w', '/web-server',
                                 'node:18',
                                 'npm', 'i'])
        if result.returncode == 0:
            ch_in.write_web_server_flag_file()
            print_success('Web-server dependencies were installed successfully.')
        else:
            print_error('Installation of web-server dependencies has failed.')
            print('Removing incomplete installation')
            delete_dir('/web-server/node_modules/')
            ch_in.delete_web_server_flag_file_if_exists()
            is_error = True
    else:
        print_info('Web-server dependencies are already installed.')

    if not ch_in.is_front_login_deps_installed() or args.force:
        print_header('Installing front-login dependencies...')
        if args.force:
            delete_dir('/front-login/node_modules')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/front-login:/front-login',
                                 '-w', '/front-login',
                                 'node:18',
                                 'npm', 'i'])
        if result.returncode == 0:
            ch_in.write_front_login_flag_file()
            print_success('Front-login dependencies were installed successfully.')
        else:
            print_error('Installation of front-login dependencies has failed.')
            print('Removing incomplete installation')
            delete_dir('/front-login/node_modules')
            ch_in.delete_front_login_flag_file_if_exists()
            is_error = True
    else:
        print_info('Front-login dependencies are already installed.')

    if not ch_in.is_core_logic_deps_installed() or args.force:
        print_header('Installing game-logic-core dependencies...')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/game-logic-core:/fwc',
                                 '-w', '/fwc',
                                 'sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_2.12.19',
                                 'sbt', 'update'])
        if result.returncode == 0:
            ch_in.write_core_logic_deps_flag_file()
            print_success('Game-logic-core dependencies were installed successfully.')
        else:
            print_error('Installation of game-logic-core dependencies has failed.')
            ch_in.delete_core_logic_flag_file_if_exists()
            is_error = True
    else:
        print_info('Game-logic-core dependencies are already installed.')

    if not ch_in.is_bob_downloaded() or args.force:
        print_header('Downloading Bob the builder - the Defold CLI building tool...')
        try:
            url = download_bob()
        except Exception as inst:
            ch_in.delete_bob_downloaded_flag_file_if_exists()
            print_error("Downloading Bob failed...")
            is_error = True
            print(inst)
        else:
            ch_in.write_bob_downloaded_flag_file(url)
            print_success("Bob was successfully downloaded.")
    else:
        print_info('Bob the builder is already downloaded.')

    if not does_docker_image_sbt_xrandr_exists() or args.force:
        print_header('Building docker image for Bob...')
        result = subprocess.run(['docker', 'build',
                                 '--tag', 'sbt_xrandr',
                                 '../../game-logic-core/docker/'])
        if result.returncode == 0:
            print_success('Docker image for Bob the builder was successfully built.')
        else:
            print_error('Building of docker image for Bob the builder has failed.')
            is_error = True
    else:
        print_info('Docker image for Bob the builder was built already.')

    if not ch_in.are_docker_images_pulled() or args.force:
        print_header('Pulling project\'s docker images and building containers...')
        os.chdir(proj_root)
        result = subprocess.run(['docker', 'compose',
                                 '-f', 'docker-compose.test.yml',
                                 'up', '--no-start'])
        os.chdir(os.path.dirname(os.path.abspath(__file__)))
        if result.returncode != 0:
            print_error('Could not pull docker images.')
            is_error = True
            ch_in.delete_docker_images_pulled_flag_file_if_exists()
        else:
            ch_in.write_docker_images_pulled_flag_file()
            print_success("Pulling docker images and building containers were successful.")
    else:
        print_info('Project\'s images were already pulled and containers were already built.')

    db_seeding_retry_failed = False

    def seed_db(second_try=False):
        if not ch_in.is_db_seeded() or args.force:
            print_header('Seeding the database...')
            result = subprocess.run(['docker', 'start',
                                     get_project_dir_name() + '-mysql-1'])
            if result.returncode != 0:
                print_error('Could not start the database image.')
                is_error = True
            else:
                result = subprocess.run(['docker', 'run', '--rm',
                                         '-v', proj_root + '/web-server:/web-server',
                                         '-w', '/web-server',
                                         '--network', get_project_dir_name() + '_net1',
                                         '-e', 'DB_HOST=mysql',
                                         'node:18',
                                         'npm', 'run', 'seed:refresh'])
                if result.returncode == 0:
                    ch_in.write_db_seeded_flag_file()
                    print_success('The database was seeded successfully.')
                else:
                    ch_in.delete_db_seeded_flag_file_if_exists()
                    print_error('There was an error seeding the database.')
                    print_warning("For whatever reason seeding the DB may fail the first time you run the script.\n"
                                  + "It could be that it takes too much time for MySQL to start the 1st time"
                                  + " or something.\n")
                    if not second_try:
                        print_info('Retrying to seed the database...')
                        seed_db(second_try=True)

                    nonlocal db_seeding_retry_failed
                    if second_try:
                        db_seeding_retry_failed = True

                    if second_try and db_seeding_retry_failed:
                        nonlocal is_error
                        is_error = True
                subprocess.run(['docker', 'stop',
                                get_project_dir_name() + '-mysql-1'])
        else:
            print_info('Database was seeded already.')

    seed_db()

    if is_error:
        exit(1)
    else:
        exit(0)


if __name__ == '__main__':
    start()
