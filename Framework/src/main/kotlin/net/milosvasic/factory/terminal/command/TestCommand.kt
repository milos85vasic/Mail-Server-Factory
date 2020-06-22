package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class TestCommand(what: String) : TerminalCommand(Commands.test(what))