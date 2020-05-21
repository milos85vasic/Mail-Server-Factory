package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class ApplicationInfoCommand(val application: String) : TerminalCommand(Commands.getApplicationInfo(application))