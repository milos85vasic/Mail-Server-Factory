package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

class GenerateRequestKeyCommand (path: String, keyName: String, reqMame: String) : TerminalCommand(

        Commands.generateRequestKey(path, keyName, reqMame)
)