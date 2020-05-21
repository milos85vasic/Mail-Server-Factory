package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.terminal.TerminalCommand

class RawTerminalCommand(
        command: String,
        configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap()

) : TerminalCommand(command, configuration)