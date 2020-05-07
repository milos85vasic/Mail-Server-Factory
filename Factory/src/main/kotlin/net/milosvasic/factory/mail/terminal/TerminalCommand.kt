package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.Configuration

class TerminalCommand(
        val command: String,
        val configuration: MutableMap<Configuration, Boolean> = Configuration.DEFAULT.toMutableMap()

) : Command(command)