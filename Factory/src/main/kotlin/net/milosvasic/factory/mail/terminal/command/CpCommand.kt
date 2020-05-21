package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class CpCommand(what: String, where: String) : TerminalCommand(Commands.cp(what, where))