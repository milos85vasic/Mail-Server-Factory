package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class PingCommand(host: String, timeoutInSeconds: Int = 3) : TerminalCommand(Commands.ping(host, timeoutInSeconds))