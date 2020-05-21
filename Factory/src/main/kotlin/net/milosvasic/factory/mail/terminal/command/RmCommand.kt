package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class RmCommand(what: String) : TerminalCommand(Commands.rm(what))