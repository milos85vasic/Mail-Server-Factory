package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand

open class SSHCommand(
    remote: Remote,
    val command: TerminalCommand
) : Command(

    Commands.ssh(
        remote.account,
        command.command,
        remote.port,
        remote.host
    )
) {

    fun getCommand() = toExecute
}