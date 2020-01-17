import sys
from pwd import *

from Toolkit.commands import *
from Toolkit.system_configuration import *
from Toolkit.configuration import *
from Toolkit.git_info import *


def get_rep_fusion_link(free=True):
    os_name = get_os_name()
    if os_centos_8 == os_name:
        if free:
            return rpm_fusion_free_8
        else:
            return rpm_fusion_non_free_8
    if free:
        return rpm_fusion_free_7
    else:
        return rpm_fusion_non_free_7


def install_rpm_fusion():
    return get_package_inallation_cmd() + " localinstall -y --nogpgcheck " + get_rep_fusion_link() + " " + get_rep_fusion_link(free=False)


def run_add_account():
    python_installation_package = get_python_installation_package()
    steps = [
        run_as_su(
            concatenate(
                add_group(mail_server_factory_group),
                install_rpm_fusion(),
                install_package_group(dnf_package_group_development),
                install_package(  # TODO: Remove unused dependencies.
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
                    "autoconf",
                    "bzip2-devel",
                    "libcurl-devel",
                    "libicu-devel",
                    "gcc-c++",
                    "libmcrypt-devel",
                    "libwebp-devel",
                    "pam.i686",
                    "pam-devel.i686",
                    "pam-devel",
                    python_installation_package
                )
            )
        )
    ]

    run(steps)

    set_git_info()
    git_configuration = get_git_info()
    init_system_configuration(
        sys.argv, 
        mail_server_factory_configuration_dir, 
        mail_server_factory_group
    )
    account_data = get_account()
    password = ""
    account = account_data[key_account]
    if key_password in account_data:
        password = account_data[key_password]


    # steps = [
    #     run_as_user_with_password(
    #         account, passwd, "echo 'X X X X X X X X X X X X X X'"
    #     )
    # ]

    run(steps)

    add_user_cmd = add_user(account)
    passwd_cmd = passwd(account)
    if password:
        add_user_cmd = add_user_with_password(account, password)
        passwd_cmd = ""
    try:
        getpwnam(account)
        print("Account already exists: " + account)
    except KeyError:
        steps = [
            run_as_su(
                concatenate(
                    home(),
                    add_user_cmd,
                    passwd_cmd,
                    add_to_group(account, mail_server_factory_group),
                    chgrp(mail_server_factory_group, mail_server_factory_configuration_dir),
                    cd(get_home_directory_path(account)),
                    mkdir(mail_server_factory),
                    cd(mail_server_factory),
                    git_clone_to_recursive(git_configuration[key_repository], here),
                    git_checkout(git_configuration[key_branch]),
                    git_submodule_checkout_each(),
                    back(),
                    chown(account, get_home_directory_path(account)),
                    chgrp(account, get_home_directory_path(account)),
                    chmod(get_home_directory_path(account), "750"),
                    home(),
                    cd(mail_server_factory),

                    # TODO:
                    # python(starter_init_script),
                    home()
                )
            ),
            run_as_user(
                account,
                concatenate(
                    cd(get_home_directory_path(account)),
                    cd(mail_server_factory),
                    python(factory_script)
                )
            )
        ]

        run(steps)


if __name__ == '__main__':
    run_add_account()
