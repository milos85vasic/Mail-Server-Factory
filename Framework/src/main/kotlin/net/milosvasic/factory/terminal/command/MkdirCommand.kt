package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class MkdirCommand(path: String) : TerminalCommand(Commands.mkdir(path))