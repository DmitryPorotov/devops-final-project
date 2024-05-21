import os
import datetime

web_sever_deps_installed_flag_file_name = 'web_sever_deps_installed'
front_login_deps_installed_flag_file_name = 'front_login_deps_installed'
core_logic_deps_installed_flag_file_name = 'core_logic_deps_installed'
docker_images_pulled_flag_file_name = 'docker_images_pulled'
db_seeded_flag_file_name = 'db_seeded'
bob_downloaded_flag_file_name = 'bob_downloaded'
cache_dir_name = '.cache'


def __delete_file_if_exists(path):
    path = './' + cache_dir_name + '/' + path
    if os.path.exists(path):
        os.remove(path)


def __write_timestamp_file(path):
    f = open('./' + cache_dir_name + '/' + path, 'w')
    f.write(datetime.datetime.now().isoformat())
    f.close()


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


def is_db_seeded():
    return os.path.exists('./' + cache_dir_name + '/' + db_seeded_flag_file_name)


def are_docker_images_pulled():
    return os.path.exists('./' + cache_dir_name + '/' + docker_images_pulled_flag_file_name)


def write_docker_images_pulled_flag_file():
    __write_timestamp_file(docker_images_pulled_flag_file_name)


def delete_docker_images_pulled_flag_file_if_exists():
    __delete_file_if_exists(docker_images_pulled_flag_file_name)


def write_web_server_flag_file():
    __write_timestamp_file(web_sever_deps_installed_flag_file_name)


def delete_web_server_flag_file_if_exists():
    __delete_file_if_exists(web_sever_deps_installed_flag_file_name)


def write_front_login_flag_file():
    __write_timestamp_file(front_login_deps_installed_flag_file_name)


def delete_front_login_flag_file_if_exists():
    __delete_file_if_exists(front_login_deps_installed_flag_file_name)


def write_core_logic_deps_flag_file():
    __write_timestamp_file(core_logic_deps_installed_flag_file_name)


def delete_core_logic_flag_file_if_exists():
    __delete_file_if_exists(core_logic_deps_installed_flag_file_name)


def write_bob_downloaded_flag_file(jar_url):
    f = open('./' + cache_dir_name + '/' + bob_downloaded_flag_file_name, 'w')
    f.write(datetime.datetime.now().isoformat() + " - " + jar_url)
    f.close()


def delete_bob_downloaded_flag_file_if_exists():
    __delete_file_if_exists(bob_downloaded_flag_file_name)


def write_db_seeded_flag_file():
    __write_timestamp_file(db_seeded_flag_file_name)


def delete_db_seeded_flag_file_if_exists():
    __delete_file_if_exists(db_seeded_flag_file_name)

