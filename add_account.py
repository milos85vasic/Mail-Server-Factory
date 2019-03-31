import sys
import pwd
from Toolkit.system_configuration import *
from configuration import *
from Toolkit.git_info import *


def run_add_account():
    steps = [
        run_as_su(
            add_group(mail_server_factory_group)
        )
    ]

    run(steps)

    set_git_info()
    git_configuration = get_git_info()
    init_system_configuration(sys.argv, mail_server_factory_configuration_dir)
    account = get_account()

    try:
        pwd.getpwnam(account)
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
