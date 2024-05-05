import os
import datetime

web_sever_deps_installed_flag_file_name = 'web_sever_deps_installed'
front_login_deps_installed_flag_file_name = 'front_login_deps_installed'
cache_dir_name = '.cache'


def web_sever_deps_installed():
    return os.path.exists('./' + cache_dir_name + '/' + web_sever_deps_installed_flag_file_name)


def front_login_deps_installed():
    return os.path.exists('./' + cache_dir_name + '/' + front_login_deps_installed_flag_file_name)


def write_web_server_flag_file():
    f = open('./' + cache_dir_name + '/' + web_sever_deps_installed_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat())
    f.close()


def write_front_login_flag_file():
    f = open('./' + cache_dir_name + '/' + front_login_deps_installed_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat())
    f.close()

