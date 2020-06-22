package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.terminal.TerminalCommand

class ScpCommand(what: String, where: String, remote: Remote) : TerminalCommand(Commands.scp(what, where, remote))