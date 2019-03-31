import getpass
from Toolkit.commands import *
from configuration import *

account = getpass.getuser()


def user_home():
    return get_home_directory_path(account)

