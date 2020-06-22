package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class PingCommand(host: String, timeoutInSeconds: Int = 3) : TerminalCommand(Commands.ping(host, timeoutInSeconds))