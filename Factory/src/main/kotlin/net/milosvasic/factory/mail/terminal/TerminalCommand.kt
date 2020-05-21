package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.operation.command.Command
import net.milosvasic.factory.mail.operation.command.CommandConfiguration

abstract class TerminalCommand(
        val command: String,
        val configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap()

) : Command(command)