package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

open class SSHCommand(
        remote: Remote,
        command: TerminalCommand,
        configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap()

) : TerminalCommand(

        Commands.ssh(
                remote.account,
                command.command,
                remote.port,
                remote.host
        ),
        configuration
)