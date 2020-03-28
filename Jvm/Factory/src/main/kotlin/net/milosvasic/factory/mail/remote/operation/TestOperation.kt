package net.milosvasic.factory.mail.remote.operation

import net.milosvasic.factory.mail.remote.ssh.SSHRemote
import net.milosvasic.factory.mail.terminal.Command
import net.milosvasic.factory.mail.terminal.Commands

class TestOperation(remote: SSHRemote) : Command(
    Commands.ssh(
        remote.username,
        Commands.echo("Hello"),
        remote.port,
        remote.host
    )
)