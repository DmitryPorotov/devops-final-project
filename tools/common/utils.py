import os


def get_project_dir_name() -> str:
    return os.path.dirname(os.path.abspath(__file__)).split('/')[-3]


def get_project_path():
    return '/'.join(os.path.dirname(os.path.abspath(__file__)).split('/')[:-2])
