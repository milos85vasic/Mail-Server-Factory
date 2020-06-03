package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class PortTakenCommand(port: Int) : TerminalCommand(Commands.portTaken(port))