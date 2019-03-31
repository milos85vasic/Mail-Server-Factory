from os.path import expanduser
from Toolkit.configuration import *

dovecot = "Dovecot"
postfix = "Postfix"

postfix_source = "http://mirror.lhsolutions.nl/postfix-release/official/postfix-3.4.0.tar.gz"
dovecot_source = "https://www.dovecot.org/releases/2.3/dovecot-2.3.4.1.tar.gz"


def dovecot_dir_path(home_path):
    return home_path + "/" + dovecot


def postfix_dir_path(home_path):
    return home_path + "/" + postfix
