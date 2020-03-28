package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.terminal.Commands

class SSHCommand(
    remote: SSHRemote,
    val command: String
) : Command(

    Commands.ssh(
        remote.username,
        command,
        remote.port,
        remote.host
    )
)