package net.milosvasic.factory.mail.operation

import net.milosvasic.factory.mail.remote.ssh.SSHRemote
import net.milosvasic.factory.mail.terminal.Commands

class TestOperation(remote: SSHRemote) : Command(
    Commands.ssh(
        remote.username,
        Commands.echo("Hello"),
        remote.port,
        remote.host
    )
)