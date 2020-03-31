package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.Commands

open class SSHCommand(
    remote: Remote,
    val command: String,
    obtainCommandOutput: Boolean = false
) : Command(

    Commands.ssh(
        remote.account,
        command,
        remote.port,
        remote.host
    ),
    obtainCommandOutput
)