package net.milosvasic.factory.component.installer.step.certificate

import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

class SignRequestKeyCommand(name: String) : TerminalCommand(

        Commands.signRequestKey(name)
)