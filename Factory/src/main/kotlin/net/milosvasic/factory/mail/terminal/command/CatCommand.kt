package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class CatCommand(what: String) : TerminalCommand(Commands.cat(what))