package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class HostNameSetCommand(hostname: String) : TerminalCommand(Commands.setHostName(hostname))