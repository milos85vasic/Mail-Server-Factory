package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

class ImportRequestKeyCommand(path: String, requestKey: String, name: String) : TerminalCommand(

        Commands.importRequestKey(path, requestKey, name)
)