package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class LinkCommand(what: String, where: String) : TerminalCommand(Commands.link(what, where))