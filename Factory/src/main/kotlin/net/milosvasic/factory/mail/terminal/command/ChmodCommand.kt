package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class ChmodCommand(where: String, mode: String) : TerminalCommand(Commands.chmod(where, mode))