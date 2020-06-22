package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class FindAndRemoveCommand(what: String, where: String) :
        TerminalCommand("${Commands.find(what, where)} -exec ${Commands.rm} {} \\;")