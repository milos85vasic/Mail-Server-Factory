package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

class GeneratePrivateKeyCommand(path: String, name: String) : TerminalCommand(

        Commands.generatePrivateKey(path, name)
)