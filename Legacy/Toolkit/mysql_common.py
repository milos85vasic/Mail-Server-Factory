import sys

from .commands import *
from .mysql_common_5560 import *

mysql_init_tmp = "init.tmp"

def get_account_from_sys_argv():
    return sys.argv[1]


def user_home():
    return get_home_directory_path(get_account_from_sys_argv())


def get_mysql_bin_directory():
    # MySQL 8.0:
    # return user_home() + "/" + mysql + "/" + mysql_installation_dir + "/usr/local/mysql/bin"
    # MySQL 5.5.60:
    return user_home() + "/" + mysql + "/" + mysql_bin_dir


def get_mysql_logs_directory():
    return user_home() + "/" + mysql + "/" + mysql_log_dir


def get_start_command(account_home):
    return "/mysqld --defaults-extra-file=" + account_home + "/" + mysql + "/" + mysql_conf_dir + "/my.cnf &"


def get_start_command_init(account_home):
    return "/mysqld --defaults-extra-file=" + account_home + "/" + mysql + "/" + mysql_conf_dir \
           + "/my.cnf --init-file=" + account_home + "/" + apache_factory + "/" + mysql_init_tmp + " &"

# MySQL 8.0:
def initialize_mysql_8():
    return "/mysqld --defaults-file=" + user_home() + "/" + mysql + "/" + mysql_conf_dir + \
             "/my.cnf --initialize --user=" + get_account_from_sys_argv()

