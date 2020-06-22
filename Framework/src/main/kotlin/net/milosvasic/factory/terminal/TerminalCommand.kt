package net.milosvasic.factory.terminal

import net.milosvasic.factory.operation.command.Command
import net.milosvasic.factory.operation.command.CommandConfiguration

abstract class TerminalCommand(
        open val command: String,
        val configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap()

) : Command(command)