package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class EchoCommand(val what: String) : TerminalCommand(Commands.echo(what))