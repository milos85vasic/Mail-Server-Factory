package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand

open class SSHCommand(
    remote: Remote,
    val command: TerminalCommand,
    obtainCommandOutput: Boolean = false
) : Command(

    Commands.ssh(
        remote.account,
        command.toExecute,
        remote.port,
        remote.host
    ),
    obtainCommandOutput
)