package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

open class SSHCommand(
        remote: Remote,
        val remoteCommand: TerminalCommand,
        configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap(),

        sshCommand: String = Commands.ssh(
                remote.account,
                remoteCommand.command,
                remote.port,
                remote.host
        )
) : TerminalCommand(sshCommand, configuration)