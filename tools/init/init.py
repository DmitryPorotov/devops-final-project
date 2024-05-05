#!/usr/bin/python3

import argparse
import os
import subprocess
import shutil
from colors import print_error, print_success
from check_deps import check_docker, check_docker_compose
from check_installs import (web_sever_deps_installed,
                            front_login_deps_installed,
                            write_front_login_flag_file,
                            write_web_server_flag_file)

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

    if not web_sever_deps_installed() or args.force:
        print('Installing web-server dependencies...')
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
            shutil.rmtree(proj_root + '/web-server/node_modules/')
    else:
        print('Web-server dependencies are already installed.')

    if not front_login_deps_installed() or args.force:
        print('Installing front-login dependencies...')
        result = subprocess.run(['docker', 'run',
                                 '-v', proj_root + '/front-login:/front-login',
                                 '-w', '/front-login',
                                 'node:18',
                                 'npm', 'i'])
        if result.returncode == 0:
            write_front_login_flag_file()
            print_success('Front-login dependencies were installed successfully.')
        else:
            print('Removing incomplete installation')
            shutil.rmtree(proj_root + '/front-login/node_modules/')
            print_error('Installation of front-login dependencies has failed.')
    else:
        print('Front-login dependencies are already installed.')


if __name__ == '__main__':
    start()
