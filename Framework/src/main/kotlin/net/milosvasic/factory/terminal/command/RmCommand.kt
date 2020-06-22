package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class RmCommand(what: String) : TerminalCommand(Commands.rm(what))