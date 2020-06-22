package net.milosvasic.factory.component.installer.step.certificate

import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

class GeneratePrivateKeyCommand(path: String, name: String) : TerminalCommand(

        Commands.generatePrivateKey(path, name)
)