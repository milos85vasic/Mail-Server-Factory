import sys
from commands import *
from configuration import *

branch = "master"

if __name__ == '__main__':
    what = sys.argv[1]

    if len(sys.argv) >= 3:
        branch = sys.argv[2]

    if what == apache_factory:
        print("Apache server factory application recognized.")
        steps = [
            run_as_su(
                concatenate(
                    cd("/root"),
                    mkdir(apache_factory),
                    cd(apache_factory),
                    git_clone_to_recursive_submodules("https://github.com/milos85vasic/Apache-Factory.git"),
                    git_checkout(branch)
                )
            )
        ]

        run(steps)
        exit()

    if what == pyramid_factory:
        print("Pyramid factory application recognized.")
        steps = [
            run_as_su(
                concatenate(
                    cd("/root"),
                    mkdir(pyramid_factory),
                    cd(pyramid_factory),
                    git_clone_to_recursive_submodules("https://github.com/milos85vasic/Pyramid-Factory.git"),
                    git_checkout(branch)
                )
            )
        ]

        run(steps)
        exit()

    if what == mail_server_factory:
        print("Mail server factory application recognized.")
        steps = [
            run_as_su(
                concatenate(
                    cd("/root"),
                    mkdir(mail_server_factory),
                    cd(mail_server_factory),
                    git_clone_to_recursive_submodules("https://github.com/milos85vasic/Mail-Server-Factory"),
                    git_checkout(branch)
                )
            )
        ]

        run(steps)
        exit()

    print("Not recognized: " + what)
    exit(1)
