package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubSSHCommand(
        command: String,
        configuration: MutableMap<CommandConfiguration, Boolean> = CommandConfiguration.DEFAULT.toMutableMap()

) : TerminalCommand(command, configuration)