#!/usr/bin/python3

import argparse
import os
import subprocess
from colors import print_error, print_success
from check_deps import check_docker, check_docker_compose
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
                            delete_web_server_flag_file_if_exists)
from defold_bob import download_bob

parser = argparse.ArgumentParser('Table games initiator')
parser.add_argument('-f', '--force', action='store_true')
args = parser.parse_args()


def start():
    if not check_docker():
        print_error('Docker is not installed')
        exit(1)
    if not check_docker_compose():
        print_error('Docker compose is not installed')
        exit(1)
    if not os.path.exists('./.cache'):
        os.mkdir('./.cache')

    proj_root = '/'.join(os.getcwd().split('/')[:-2])

    def delete_dir(dir_):
        subprocess.run(['docker', 'run',
                        '-v', proj_root + '/web-server:/web-server',
                        '-w', '/web-server',
                        'node:18',
                        'rm', '-rf', dir_])

    if not is_web_sever_deps_installed() or args.force:
        print('Installing web-server dependencies...')
        if args.force:
            delete_dir('/web-server/node_modules/')
        result = subprocess.run(['docker', 'run',
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
    else:
        print('Web-server dependencies are already installed.')

    if not is_front_login_deps_installed() or args.force:
        print('Installing front-login dependencies...')
        if args.force:
            delete_dir('/front-login/node_modules')
        result = subprocess.run(['docker', 'run',
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
    else:
        print('Front-login dependencies are already installed.')

    if not is_core_logic_deps_installed() or args.force:
        print('Installing game-logic-core dependencies...')
        result = subprocess.run(['docker', 'run',
                                 '-v', proj_root + '/game-logic-core:/fwc',
                                 '-w', '/fwc',
                                 'sbtscala/scala-sbt:eclipse-temurin-alpine-17.0.10_7_1.9.9_3.4.1',
                                 'sbt', 'update'])
        if result.returncode == 0:
            write_core_logic_deps_flag_file()
            print_success('Game-logic-core dependencies were installed successfully.')
        else:
            print_error('Installation of game-logic-core dependencies has failed.')
            delete_core_logic_flag_file_if_exists()
    else:
        print('Game-logic-core dependencies are already installed.')

    if not is_bob_downloaded() or args.force:
        print('Downloading Bob the builder - the Defold CLI building tool...')
        try:
            url = download_bob()
        except Exception as inst:
            delete_bob_downloaded_flag_file_if_exists()
            print_error("Downloading Bob failed...")
            print(inst)
        else:
            write_bob_downloaded_flag_file(url)
            print_success("Bob was successfully downloaded.")


if __name__ == '__main__':
    start()
