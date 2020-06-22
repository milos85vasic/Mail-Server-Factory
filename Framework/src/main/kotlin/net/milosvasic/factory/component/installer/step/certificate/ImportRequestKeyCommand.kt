package net.milosvasic.factory.component.installer.step.certificate

import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

class ImportRequestKeyCommand(path: String, requestKey: String, name: String) : TerminalCommand(

        Commands.importRequestKey(path, requestKey, name)
)