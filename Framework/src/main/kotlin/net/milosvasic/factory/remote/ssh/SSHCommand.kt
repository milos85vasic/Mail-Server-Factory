package net.milosvasic.factory.remote.ssh

import net.milosvasic.factory.operation.command.CommandConfiguration
import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

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