package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.operation.Command

class TerminalCommand(val command: String, var obtainOutput: Boolean = false) : Command(command)