package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class PortTakenCommand(port: Int) : TerminalCommand(Commands.portTaken(port))