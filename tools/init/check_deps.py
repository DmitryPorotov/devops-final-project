import subprocess


def check_docker():
    result = subprocess.run(['docker', '-v'], stdout=-3)
    return result.returncode == 0


def check_docker_compose():
    result = subprocess.run(['docker', 'compose'], stdout=-3)
    return result.returncode == 0
