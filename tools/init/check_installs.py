import os
import datetime

web_sever_deps_installed_flag_file_name = 'web_sever_deps_installed'
front_login_deps_installed_flag_file_name = 'front_login_deps_installed'
core_logic_deps_installed_flag_file_name = 'core_logic_deps_installed'
bob_downloaded_flag_file_name = 'bob_downloaded'
cache_dir_name = '.cache'


def is_web_sever_deps_installed():
    return os.path.exists('./' + cache_dir_name + '/' + web_sever_deps_installed_flag_file_name)


def is_front_login_deps_installed():
    return os.path.exists('./' + cache_dir_name + '/' + front_login_deps_installed_flag_file_name)


def is_core_logic_deps_installed():
    return os.path.exists('./' + cache_dir_name + '/' + core_logic_deps_installed_flag_file_name)


def is_bob_downloaded():
    flag = os.path.exists('./' + cache_dir_name + '/' + bob_downloaded_flag_file_name)
    bob = os.path.exists('./' + cache_dir_name + '/bob.jar')
    return bob and flag


def write_web_server_flag_file():
    f = open('./' + cache_dir_name + '/' + web_sever_deps_installed_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat())
    f.close()


def delete_web_server_flag_file_if_exists():
    path = './' + cache_dir_name + '/' + web_sever_deps_installed_flag_file_name
    if os.path.exists(path):
        os.remove(path)


def write_front_login_flag_file():
    f = open('./' + cache_dir_name + '/' + front_login_deps_installed_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat())
    f.close()


def delete_front_login_flag_file_if_exists():
    path = './' + cache_dir_name + '/' + front_login_deps_installed_flag_file_name
    if os.path.exists(path):
        os.remove(path)


def write_core_logic_deps_flag_file():
    f = open('./' + cache_dir_name + '/' + core_logic_deps_installed_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat())
    f.close()


def delete_core_logic_flag_file_if_exists():
    path = './' + cache_dir_name + '/' + core_logic_deps_installed_flag_file_name
    if os.path.exists(path):
        os.remove(path)


def write_bob_downloaded_flag_file(jar_url):
    f = open('./' + cache_dir_name + '/' + bob_downloaded_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat() + " - " + jar_url)
    f.close()


def delete_bob_downloaded_flag_file_if_exists():
    path = './' + cache_dir_name + '/' + bob_downloaded_flag_file_name
    if os.path.exists(path):
        os.remove(path)
