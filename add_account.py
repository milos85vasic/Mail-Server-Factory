import sys
from pwd import *
from Toolkit.commands import *
from Toolkit.system_configuration import *
from configuration import *
from Toolkit.git_info import *


def run_add_account():

    virtualenv = "virtualenv"
    temp_python = "temp-python"
    steps = [
        run_as_su(
            concatenate(
                add_group(mail_server_factory_group),
                "yum localinstall -y --nogpgcheck " + rpm_fusion_free + " " + rpm_fusion_non_free,
                get_yum_group("Development Tools"),
                get_yum(  # TODO: Remove unused dependencies.
                    "epel-release",
                    "openssl-devel",
                    "gcc",
                    "make",
                    "cmake",
                    "automake",
                    "libtool",
                    "wget",
                    "git",
                    "libxml2",
                    "libxml2-devel",
                    "ncurses-devel",
                    "lynx",
                    "links",
                    "autoconf",
                    "re2c",
                    "bzip2-devel",
                    "libcurl-devel",
                    "libicu-devel",
                    "gcc-c++",
                    "libmcrypt-devel",
                    "libwebp-devel",
                    "pam.i686",
                    "pam-devel.i686",
                    "pam-devel",
                    "python36",
                    "python3-pip"
                ),
                pip_upgrade(),
                pip(virtualenv),
                venv_init_version(2, temp_python),
                venv_activate_name(temp_python)
            )
        )
    ]

    run(steps)

    set_git_info()
    git_configuration = get_git_info()
    init_system_configuration(sys.argv, mail_server_factory_configuration_dir)
    account = get_account()

    try:
        getpwnam(account)
        print("Account already exists: " + account)
    except KeyError:
        steps = [
            run_as_su(
                concatenate(
                    cd("~"),
                    add_user(account),
                    passwd(account),
                    add_to_group(account, mail_server_factory_group),
                    chgrp(mail_server_factory_group, mail_server_factory_configuration_dir),
                    cd(get_home_directory_path(account)),
                    mkdir(mail_server_factory),
                    cd(mail_server_factory),
                    git_clone_to_recursive(git_configuration[key_repository], here),
                    git_checkout(git_configuration[key_branch]),
                    git_submodule_checkout_each(),
                    cd(".."),  # TODO: Refactor into 'back' variable.
                    chown(account, get_home_directory_path(account)),
                    chgrp(account, get_home_directory_path(account)),
                    chmod(get_home_directory_path(account), "750"),
                    cd("~"),
                    cd(mail_server_factory),

                    # TODO:
                    # python(starter_init_script),
                    cd("~")
                )
            ),
            run_as_user(
                account,
                concatenate(
                    python(factory_script)
                )
            )
        ]

        run(steps)


if __name__ == '__main__':
    run_add_account()
