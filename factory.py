import getpass
from Toolkit.commands import *
from configuration import *

account = getpass.getuser()


def user_home():
    return get_home_directory_path(account)


def run_factory():
    steps = [

    ]

    run(steps)


if __name__ == '__main__':
    run_factory()
