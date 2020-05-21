package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class TestCommand(what: String) : TerminalCommand(Commands.test(what))