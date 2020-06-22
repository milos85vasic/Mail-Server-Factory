package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class LinkCommand(what: String, where: String) : TerminalCommand(Commands.link(what, where))