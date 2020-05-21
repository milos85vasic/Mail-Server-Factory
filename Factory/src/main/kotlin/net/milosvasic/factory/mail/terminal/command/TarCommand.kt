package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class TarCommand(what: String, where: String) : TerminalCommand(Commands.tar(what, where))