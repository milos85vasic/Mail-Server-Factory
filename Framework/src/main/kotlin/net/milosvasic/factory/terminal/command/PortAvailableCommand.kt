package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class PortAvailableCommand(port: Int) : TerminalCommand(Commands.portAvailable(port))