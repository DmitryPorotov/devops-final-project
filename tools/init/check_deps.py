import subprocess


def check_docker() -> bool:
    result = subprocess.run(['docker', '-v'], stdout=-3)
    return result.returncode == 0


def check_docker_compose() -> bool:
    result = subprocess.run(['docker', 'compose'], stdout=-3)
    return result.returncode == 0


def check_docker_group() -> bool:
    result = subprocess.run(['groups'], capture_output=True)
    return result.stdout.decode('utf8').find('docker') >= 0
