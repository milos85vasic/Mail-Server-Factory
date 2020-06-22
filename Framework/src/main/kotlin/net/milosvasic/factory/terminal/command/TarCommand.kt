package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class TarCommand(what: String, where: String) : TerminalCommand(Commands.tar(what, where))