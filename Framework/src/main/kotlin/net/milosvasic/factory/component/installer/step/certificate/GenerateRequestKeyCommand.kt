package net.milosvasic.factory.component.installer.step.certificate

import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

class GenerateRequestKeyCommand (path: String, keyName: String, reqMame: String) : TerminalCommand(

        Commands.generateRequestKey(path, keyName, reqMame)
)