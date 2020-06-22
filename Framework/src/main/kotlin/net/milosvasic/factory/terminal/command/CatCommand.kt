package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class CatCommand(what: String) : TerminalCommand(Commands.cat(what))