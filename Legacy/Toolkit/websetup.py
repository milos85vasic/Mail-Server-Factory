import os
import sys

toolkit_directory = "Toolkit"
toolkit_repo = "https://github.com/milos85vasic/Apache-Factory-Toolkit.git"

if __name__ == '__main__':
    exists = True
    steps = []
    if not os.path.exists(toolkit_directory):
        exists = False
        steps.extend(
            [
                "mkdir " + toolkit_directory,
                "git clone --recurse-submodules " + toolkit_repo + " ./" + toolkit_directory,
            ]
        )

    for cmd in steps:
        os.system(cmd)

    branch = "master"
    what = sys.argv[1]

    if len(sys.argv) >= 3:
        branch = sys.argv[2]

    from Legacy.Toolkit import get_python_cmd
    python_cmd = get_python_cmd()
    setup = python_cmd + " ./" + toolkit_directory + "/websetup_run.py " + what
    if branch is not "master":
        setup += " " + branch

    steps = [
        setup
    ]

    if not exists:
        steps.extend(
            [
                "rm -rf ./" + toolkit_directory,
                "rm -f " + os.path.basename(__file__)
            ]
        )

    for cmd in steps:
        os.system(cmd)