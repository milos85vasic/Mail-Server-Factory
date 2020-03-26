import json
import getpass
import sys

from commands import *
from configuration import *
from system_configuration import *

account = getpass.getuser()

system_configuration = get_system_configuration()

service = None
service_url = sys.argv[1]
service_home = sys.argv[2]
services = system_configuration[account][key_services][key_services]

for service_item in services:
    if service_item[key_services_url] == service_url:
        service = service_item


def get_index(directory):
    for index in service_indexes:
        full_path = directory + "/" + index
        if os.path.isfile(full_path):
            return directory
    return None


service_root_directory = get_index(service_home)
if service_root_directory is not None:
    service[key_service_root] = service_root_directory
    save_system_configuration(system_configuration)
else:
    for subdirectory, _, _ in os.walk(service_home):
        service_root_directory = get_index(subdirectory)
        if service_root_directory is not None:
            service[key_service_root] = service_root_directory
            save_system_configuration(system_configuration)
            break
