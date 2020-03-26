from .configuration import *
from .system_configuration import *


# MySQL 5.5.60:
def get_mysql_start_command(user_account):
    system_configuration = get_system_configuration()
    mysql_full_path = get_home_directory_path(user_account) + "/" + mysql + "/"
    port = default_port_mysql
    if user_account in system_configuration:
        if key_configuration_port_mysql in system_configuration[user_account]:
            port = system_configuration[user_account][key_configuration_port_mysql]

    return mysql_full_path + mysql_bin_dir + "/mysqld --tmpdir=" + mysql_full_path + "tmp --datadir=" \
           + mysql_full_path + "data " + "--secure-file-priv=" + mysql_full_path + "priv --port=" + str(port) \
           + " --user=" + user_account + " " + "--socket=" + mysql_full_path + "sock/socket &"





