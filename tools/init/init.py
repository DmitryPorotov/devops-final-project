#!/usr/bin/python3

import argparse
import os
import subprocess
from colors import print_error, print_success
from check_deps import check_docker, check_docker_compose, check_docker_group
from check_installs import (is_web_sever_deps_installed,
                            is_front_login_deps_installed,
                            write_front_login_flag_file,
                            write_web_server_flag_file,
                            write_core_logic_deps_flag_file,
                            is_core_logic_deps_installed,
                            is_bob_downloaded,
                            write_bob_downloaded_flag_file,
                            delete_bob_downloaded_flag_file_if_exists,
                            delete_core_logic_flag_file_if_exists,
                            delete_front_login_flag_file_if_exists,
                            delete_web_server_flag_file_if_exists,
                            delete_db_seeded_flag_file_if_exists,
                            is_db_seeded,
                            write_db_seeded_flag_file)
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

    if not is_web_sever_deps_installed() or args.force:
        print('Installing web-server dependencies...')
        if args.force:
            delete_dir('/web-server/node_modules/')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/web-server:/web-server',
                                 '-w', '/web-server',
                                 'node:18',
                                 'npm', 'i'])
        if result.returncode == 0:
            write_web_server_flag_file()
            print_success('Web-server dependencies were installed successfully.')
        else:
            print_error('Installation of web-server dependencies has failed.')
            print('Removing incomplete installation')
            delete_dir('/web-server/node_modules/')
            delete_web_server_flag_file_if_exists()
            is_error = True
    else:
        print('Web-server dependencies are already installed.')

    if not is_front_login_deps_installed() or args.force:
        print('Installing front-login dependencies...')
        if args.force:
            delete_dir('/front-login/node_modules')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/front-login:/front-login',
                                 '-w', '/front-login',
                                 'node:18',
                                 'npm', 'i'])
        if result.returncode == 0:
            write_front_login_flag_file()
            print_success('Front-login dependencies were installed successfully.')
        else:
            print_error('Installation of front-login dependencies has failed.')
            print('Removing incomplete installation')
            delete_dir('/front-login/node_modules')
            delete_front_login_flag_file_if_exists()
            is_error = True
    else:
        print('Front-login dependencies are already installed.')

    if not is_core_logic_deps_installed() or args.force:
        print('Installing game-logic-core dependencies...')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/game-logic-core:/fwc',
                                 '-w', '/fwc',
                                 'sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_2.12.19',
                                 'sbt', 'update'])
        if result.returncode == 0:
            write_core_logic_deps_flag_file()
            print_success('Game-logic-core dependencies were installed successfully.')
        else:
            print_error('Installation of game-logic-core dependencies has failed.')
            delete_core_logic_flag_file_if_exists()
            is_error = True
    else:
        print('Game-logic-core dependencies are already installed.')

    if not is_bob_downloaded() or args.force:
        print('Downloading Bob the builder - the Defold CLI building tool...')
        try:
            url = download_bob()
        except Exception as inst:
            delete_bob_downloaded_flag_file_if_exists()
            print_error("Downloading Bob failed...")
            is_error = True
            print(inst)
        else:
            write_bob_downloaded_flag_file(url)
            print_success("Bob was successfully downloaded.")
    else:
        print('Bob the builder is already downloaded.')

    if not does_docker_image_sbt_xrandr_exists() or args.force:
        print('Building docker image for Bob...')
        result = subprocess.run(['docker', 'build',
                                 '--tag', 'sbt_xrandr',
                                 '../../game-logic-core/docker/'])
        if result.returncode == 0:
            print_success('Docker image for Bob the builder was successfully built.')
        else:
            print_error('Building of docker image for Bob the builder has failed.')
            is_error = True
    else:
        print('Docker image for Bob the builder was built already.')

    if not is_db_seeded() or args.force:
        print('Seeding the database...')
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
                write_db_seeded_flag_file()
                print_success('The database was seeded successfully.')
            else:
                delete_db_seeded_flag_file_if_exists()
                print_error('There was an error seeding the database.')
                is_error = True
            subprocess.run(['docker', 'stop',
                            'table-games-monorepo-mysql-1'])
    else:
        print('Database was seeded already.')

    if is_error:
        exit(1)
    else:
        exit(0)


if __name__ == '__main__':
    start()
