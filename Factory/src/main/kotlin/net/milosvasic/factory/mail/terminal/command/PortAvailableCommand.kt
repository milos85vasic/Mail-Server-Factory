package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class PortAvailableCommand(port: Int) : TerminalCommand(Commands.portAvailable(port))