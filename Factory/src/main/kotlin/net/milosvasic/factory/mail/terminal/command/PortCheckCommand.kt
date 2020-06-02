package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class PortCheckCommand(port: Int) : TerminalCommand(Commands.portCheck(port))