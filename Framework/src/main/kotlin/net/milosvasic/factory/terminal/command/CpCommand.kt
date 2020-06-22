package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class CpCommand(what: String, where: String) : TerminalCommand(Commands.cp(what, where))