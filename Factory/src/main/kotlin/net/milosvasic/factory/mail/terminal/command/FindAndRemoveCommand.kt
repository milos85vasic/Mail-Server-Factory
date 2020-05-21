package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class FindAndRemoveCommand(what: String, where: String) :
        TerminalCommand("${Commands.find(what, where)} -exec ${Commands.rm} {} \\;")