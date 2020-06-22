package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class ApplicationInfoCommand(val application: String) : TerminalCommand(Commands.getApplicationInfo(application))