from Legacy.Toolkit import *

arg_prefix = "--"
arg_server_admin = arg_prefix + "server_admin"
key_configuration_port = "port"
key_configuration_port_mysql = "port_mysql"
key_configuration_port_postfix = "port_postfix"
key_configuration_port_postfix_secure = "port_postfix_secure"
key_configuration_port_postfix_submission = "port_postfix_submission"
key_configuration_port_dovecot = "port_dovecot"
key_configuration_port_dovecot_secure = "port_dovecot_secure"
key_configuration_server_admin = "server_admin"
key_services = "services"
key_features = "features"
feature_mysql = "mysql"
feature_php_5 = "php_5"
key_services_url = "url"
key_services_urls = "urls"
key_service_root = "root"
key_credentials = "credentials"
key_services_repository = "repository"
key_password_protect = "password_protect"
key_password_protect_user = "user"
key_password_protect_password = "password"
key_password_protect_directories = "directories"
key_password_protect_service = "service"
key_password_protect_path = "path"
services_file = key_services + ".json"
key_configuration = "configuration"
key_configuration_main_proxy = "main_proxy"
key_explicit_port_number = "explicit_port_number"
key_configuration_repository = "configuration_repository"


def init_system_configuration(
        arguments,
        configuration_dir=apache_factory_configuration_dir,
        configuration_group=apache_factory_group
):
    default_config_json = configuration_dir + "/global_configuration.json"
    if not os.path.isdir(configuration_dir):
        steps = [
            run_as_su(
                concatenate(
                    mkdir(configuration_dir),
                    chmod(configuration_dir, "770")
                )
            )
        ]

        run(steps)

    system_configuration = get_system_configuration(configuration_dir)
    account_data = {}
    for arg in arguments:
        if arguments.index(arg) > 0 and not str(arg).startswith(arg_prefix):
            if key_account not in account_data:
                system_configuration[arg] = {key_configuration_server_admin: "root@localhost"}
                account = arg
                account_data[key_account] = account
                save_account(account_data)
            else:
                if key_password not in account_data:
                    password = arg
                    account_data[key_password] = password
                    save_account(account_data)
        if str(arg).startswith(arg_server_admin):
            if arguments.index(arg) == 1:
                print("First argument must be name of the account!")
                exit(1)
            server_admin = str(arg).replace(arg_server_admin + "=", "")
            account = account_data[key_account]
            if account:
                system_configuration[account][key_configuration_server_admin] = server_admin
            else:
                print("No account information available to continue further [1].")
                exit(1)
    if os.path.isfile(services_file):
        services_config = json.load(open(services_file))
        account = account_data[key_account]
        if account:
            system_configuration[account][key_services] = services_config
        else:
            print("No account information available to continue further [2].")
            exit(1)
    save_system_configuration(system_configuration, configuration_dir=configuration_dir)

    steps = [
        run_as_su(
            concatenate(
                chmod(default_config_json, "770"),
                chgrp(configuration_group, default_config_json),
            )
        )
    ]

    run(steps)

    return system_configuration


def get_account():
    return json.load(open(account_json))


def get_system_configuration(configuration_dir=apache_factory_configuration_dir):
    default_config_json = configuration_dir + "/global_configuration.json"
    system_configuration = {
        key_configuration_port: default_port,
        key_configuration_port_mysql: default_port_mysql
    }
    # TODO: Refactor this:
    if configuration_dir == mail_server_factory_configuration_dir:
        until = 1000
        system_configuration = {
            key_configuration_port_postfix:
                get_first_available_port(default_port_postfix, default_port_postfix + until),
            key_configuration_port_postfix_secure:
                get_first_available_port(default_port_postfix_secure, default_port_postfix_secure + until),
            key_configuration_port_postfix_submission:
                get_first_available_port(default_port_postfix_submission, default_port_postfix_submission + until),
            key_configuration_port_dovecot:
                get_first_available_port(default_port_dovecot, default_port_dovecot + until),
            key_configuration_port_dovecot_secure:
                get_first_available_port(default_port_dovecot_secure, default_port_dovecot_secure + until)
        }
    if not os.path.isfile(default_config_json):
        try:
            with open(default_config_json, 'w') as outfile:
                json.dump(system_configuration, outfile)
        except IOError:
            print("Can't access [3]: " + default_config_json)
    else:
        system_configuration = json.load(open(default_config_json))
    return system_configuration


def save_account(account):
    with open(account_json, 'w') as outfile:
        json.dump(account, outfile)


def save_system_configuration(system_configuration, configuration_dir=apache_factory_configuration_dir):
    default_config_json = configuration_dir + "/global_configuration.json"
    with open(default_config_json, 'w') as outfile:
        json.dump(system_configuration, outfile)


def get_services_directories(account, configuration_dir=apache_factory_configuration_dir):
    directories = []
    system_configuration = get_system_configuration(configuration_dir)
    if account in system_configuration:
        if key_services in system_configuration[account]:
            if key_services in system_configuration[account][key_services]:
                for service in system_configuration[account][key_services][key_services]:
                    directories.append(service[key_services_url])
    return directories


def has_feature(account, feature, configuration_dir=apache_factory_configuration_dir):
    features = None
    system_configuration = get_system_configuration(configuration_dir)

    account_configuration = system_configuration[account]
    if isinstance(account_configuration, dict):
        if key_services in account_configuration:
            if key_features in system_configuration[account][key_services]:
                features = system_configuration[account][key_services][key_features]

    return features and feature in features
