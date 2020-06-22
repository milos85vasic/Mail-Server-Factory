package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class UnTarCommand(what: String, where: String) : TerminalCommand(Commands.unTar(what, where))