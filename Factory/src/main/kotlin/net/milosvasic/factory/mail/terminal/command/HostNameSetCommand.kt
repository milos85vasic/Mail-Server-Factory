package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class HostNameSetCommand(hostname: String) : TerminalCommand(Commands.setHostName(hostname))