package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.operation.Command

class TerminalCommand(val command: String, obtainResultOutput: Boolean = false) :
        Command(command, obtainResultOutput)