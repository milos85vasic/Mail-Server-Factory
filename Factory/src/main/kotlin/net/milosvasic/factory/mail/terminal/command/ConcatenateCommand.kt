package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class ConcatenateCommand(vararg commands: String) : TerminalCommand(Commands.concatenate(*commands))