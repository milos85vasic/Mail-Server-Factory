import getpass
from configuration import *
from Toolkit.commands import *


account = getpass.getuser()


def user_home():
    return get_home_directory_path(account)


def run_factory():
    # TODO: --with-mysql --with-shadow --with-pam --with-nss --with-gssapi --with-vpopmail --with-sql
    dovecot_configuration = "./configure --prefix=" + user_home() + "/" + dovecot

    postfix_configuration = "./configure --prefix=" + user_home() + "/" + postfix  # + " " <- TODO.

    steps = [
        run_as_su(
            concatenate(
                add_to_group(account, mail_server_factory_group),

                # TODO: Re-check content dir need.
                mkdir(content_dir_path(user_home())),
                chown(account, content_dir_path(user_home())),
                chgrp(account, content_dir_path(user_home())),

                mkdir(dovecot_dir_path(user_home())),
                chown(account, dovecot_dir_path(user_home())),
                chgrp(account, dovecot_dir_path(user_home())),
                mkdir(postfix_dir_path(user_home())),
                chown(account, postfix_dir_path(user_home())),
                chgrp(account, postfix_dir_path(user_home())),
                run_as_user(
                    account,
                    concatenate(
                        home(),

                        wget(dovecot_source, destination=(user_home())),
                        extract(dovecot_archive, destination=(user_home())),
                        cd(dovecot_extracted_dir),
                        dovecot_configuration,
                        "make",
                        "make install",

                        home(),
                        rm(dovecot_archive),
                        rm(dovecot_extracted_dir),

                        wget(postfix_source, destination=(user_home())),
                        extract(postfix_archive, destination=(user_home())),
                        cd(postfix_extracted_dir),
                        # postfix_configuration,
                        "make",  # FIXME: No <db.h> include file found. Install the appropriate db*-devel package first.
                        # "make install",

                        home(),
                        rm(postfix_archive),
                        rm(postfix_extracted_dir),

                        # TODO:
                        # mkdir(apache_home),
                        # mkdir(apache_home + "/" + apache_vhosts_directory),
                        # mkdir(php_home),
                        # mkdir(mysql_home),
                        # cp(
                        #     content_dir_matrix_path(user_home()),
                        #     content_dir_path(user_home())
                        # ),
                        # cp(
                        #     content_dir_matrix_path_php(user_home()),
                        #     content_dir_path(user_home())
                        # ),
                        # wget(apache_download, destination=(home + "/")),
                        # extract(apache_extract, destination=home),
                        # cd(apache_extracted),
                        # "./configure --prefix=" + apache_home,
                        # "make",
                        # "make install",
                        # cd("~"),
                        # rm(apache_extracted),
                        # rm(apache_tar_gz)
                    )
                ),

                # TODO:
                # cd(home + "/" + apache_factory),
                # python(brotli_installation_script, account),
                # run_as_user(
                #     account,
                #     concatenate(
                #         cd(user_home() + "/" + apache_factory),
                #         python(password_protect_script),
                #         python(mysql_installation_script, account),
                #         python(php_installation_script, account),
                #         python(distribution_script)
                #     )
                # )

                venv_deactivate()
            )
        )
    ]

    run(steps)


if __name__ == '__main__':
    run_factory()
