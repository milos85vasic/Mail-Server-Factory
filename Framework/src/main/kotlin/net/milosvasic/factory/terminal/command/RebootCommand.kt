package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class RebootCommand(rebootScheduleTime: Int) : TerminalCommand(Commands.reboot(rebootScheduleTime))