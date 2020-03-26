import json
import getpass
import sys

from commands import *
from configuration import *
from system_configuration import *

account = getpass.getuser()

if sys.argv.__len__() >= 2:
    account = sys.argv[1]
    print("Account passed as parameter: " + account)

system_configuration = get_system_configuration()

scheduled_for_restart = []

if account in system_configuration:
    if key_services in system_configuration[account]:
        if key_services in system_configuration[account][key_services]:
            for service in system_configuration[account][key_services][key_services]:
                if key_configuration_main_proxy in service:
                    bind_to_account = service[key_configuration_main_proxy]
                    scheduled_for_restart.append(bind_to_account)
                    destination_directory = get_home_directory_path(
                        bind_to_account) + "/" + apache2 + "/" + apache_vhosts_directory

                    url = service[key_services_url]
                    urls = [url]
                    if key_services_urls in service:
                        urls.extend(service[key_services_urls])

                    if key_service_root in service:
                        root = service[key_service_root]
                        destination_file = destination_directory + "/" + url + ".conf"
                        if not os.path.isfile(destination_file):
                            try:
                                with open(destination_file, 'w') as outfile:
                                    port = str(system_configuration[account][key_configuration_port])
                                    for url in urls:
                                        outfile.write("\n")
                                        outfile.write("<VirtualHost *:80>")
                                        outfile.write("\n")
                                        outfile.write("\tProxyPreserveHost On")
                                        outfile.write("\n")
                                        outfile.write("\tProxyPass / http://127.0.0.1:" + port + "/")
                                        outfile.write("\n")
                                        outfile.write("\tProxyPassReverse / http://127.0.0.1:" + port + "/")
                                        outfile.write("\n")
                                        outfile.write("\tServerName " + url)
                                        outfile.write("\n")
                                        outfile.write("</VirtualHost>")
                                        outfile.write("\n")

                            except IOError:
                                print("Can't access [2]: " + destination_file)
                        else:
                            print("Virtual host configuration already exist: " + destination_file)
                    else:
                        print("No root for: " + url)
else:
    print("No account '" + account + "' in system configuration.")

for scheduled in scheduled_for_restart:
    steps = [
        run_as_su(
            get_home_directory_path(scheduled) + "/" + apache2 + "/bin/apachectl restart"
        )
    ]

    run(steps)
