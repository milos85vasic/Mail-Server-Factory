package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class RebootCommand(rebootScheduleTime: Int) : TerminalCommand(Commands.reboot(rebootScheduleTime))