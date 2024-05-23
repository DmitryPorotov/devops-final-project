#!/usr/bin/python3
import os
import sys
import subprocess
import argparse

parser = argparse.ArgumentParser('Table games production builder.')
parser.add_argument('-o', '--only', nargs='+', choices=['worker', 'nest', 'login', 'defold'], action='store')
args = parser.parse_args()


def start():

    proj_root = get_project_path()
    is_error = False

    if not args.only or 'worker' in args.only:
        print_header('Building the worker...')
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + '/game-logic-core:/fwc',
                                 '-w', '/fwc',
                                 'sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_2.12.19',
                                 'sbt', 'assembly'])
        if result.returncode != 0:
            print_error('Worker build failed.')
            is_error = True
        else:
            print_success('The worker jar was built successfully.')

    if not args.only or 'nest' in args.only:
        print_header("Transpiling the web server...")
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + "/web-server:/web-server",
                                 '-w', '/web-server',
                                 'node:18',
                                 'npx', 'nest', 'build'])
        if result.returncode != 0:
            print_error('Web server transpiling failed.')
            is_error = True
        else:
            print_success('Web server transpiling was successful.')

    if not args.only or 'login' in args.only:
        print_header("Building React front-end...")
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + "/front-login:/front-login",
                                 '-w', '/front-login',
                                 'node:18',
                                 'npx', 'react-scripts', 'build'])
        if result.returncode != 0:
            print_error('Building React front-end failed.')
            is_error = True
        else:
            print_success('Building React front-end successful.')

    if not args.only or 'defold' in args.only:
        print_header("Building Defold front-end...")
        result = subprocess.run(['docker', 'run', '--rm',
                                 '-v', proj_root + "/front-defold:/fd_fwc",
                                 '-v', proj_root + '/tools/init/.cache:/bob',
                                 '-w', '/fd_fwc',
                                 'sbt_xrandr',
                                 'java', '-jar', '/bob/bob.jar',
                                 '--settings=web.properties',
                                 '-bo', '/fd_fwc/build/prod/htmlLaunchDir',
                                 'build', 'bundle', '-p', 'js-web', '-a'])

        if result.returncode != 0:
            print_error('Building Defold front-end failed.')
            is_error = True
        else:
            print_success('Building Defold front-end was successful.')

    # # here or with docker compose?
    # print_header("Building Nginx static content server image...")
    # result = subprocess.run(['docker', 'run', '--rm',
    #                          '-v', proj_root + "/front-defold:/fd_fwc",
    #                          '-v', proj_root + '/tools/init/.cache:/bob',
    #                          '-w', '/fd_fwc',
    #                          'sbt_xrandr',
    #                          'java', '-jar', '/bob/bob.jar',
    #                          '--settings=web.properties',
    #                          '-bo', '/fd_fwc/build/prod/htmlLaunchDir',
    #                          'build', 'bundle', '-p', 'js-web', '-a'])
    #
    # if result.returncode != 0:
    #     print_error('Building Nginx static content server failed.')
    #     is_error = True
    # else:
    #     print_success('Building Nginx static content server was successful.')

    if is_error:
        exit(1)


if __name__ == '__main__' and not __package__:
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    sys.path.append('../../')
    __package__ = 'tools.builder'

    from ..common.colors import print_error, print_success, print_header, print_info, print_warning
    from ..common.utils import get_project_dir_name, get_project_path

    start()
