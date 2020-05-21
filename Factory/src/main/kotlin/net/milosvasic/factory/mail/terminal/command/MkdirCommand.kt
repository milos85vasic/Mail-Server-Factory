package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class MkdirCommand(path: String) : TerminalCommand(Commands.mkdir(path))