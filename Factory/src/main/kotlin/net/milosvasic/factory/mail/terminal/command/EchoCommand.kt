package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class EchoCommand(val what: String) : TerminalCommand(Commands.echo(what))