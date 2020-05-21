package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class UnTarCommand(what: String, where: String) : TerminalCommand(Commands.unTar(what, where))