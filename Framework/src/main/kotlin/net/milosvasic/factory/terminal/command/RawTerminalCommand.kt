package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.operation.command.CommandConfiguration
import net.milosvasic.factory.terminal.TerminalCommand

class RawTerminalCommand(
        command: String,
        configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap()

) : TerminalCommand(command, configuration)