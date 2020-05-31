package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

class SignRequestKeyCommand(name: String) : TerminalCommand(

        Commands.signRequestKey(name)
)