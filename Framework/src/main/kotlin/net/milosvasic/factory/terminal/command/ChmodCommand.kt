package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class ChmodCommand(where: String, mode: String) : TerminalCommand(Commands.chmod(where, mode))