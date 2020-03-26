import socket, errno


def is_port_available(port_number):
    success = True
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.bind(("127.0.0.1", port_number))
    except socket.error as e:
        if e.errno == errno.EADDRINUSE:
            success = False
            print("Port is already in use")
        else:
            print(e)
    s.close()
    if success:
        "Port " + str(port_number) + " is open."
    return success


def get_first_available_port(from_port, until_port):
    while not is_port_available(from_port) and from_port < until_port:
        from_port = from_port + 1
    return from_port
