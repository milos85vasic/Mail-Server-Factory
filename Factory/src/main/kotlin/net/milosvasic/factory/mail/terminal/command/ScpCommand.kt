package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.TerminalCommand

class ScpCommand(what: String, where: String, remote: Remote) : TerminalCommand(Commands.scp(what, where, remote))