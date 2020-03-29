package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.terminal.Commands

open class SSHCommand(
    remote: SSHRemote,
    val command: String,
    obtainCommandOutput: Boolean = false
) : Command(

    Commands.ssh(
        remote.username,
        command,
        remote.port,
        remote.host
    ),
    obtainCommandOutput
)