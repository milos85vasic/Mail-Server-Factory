package net.milosvasic.factory.mail.remote

import net.milosvasic.factory.mail.common.execution.Executor
import net.milosvasic.factory.mail.os.OperatingSystem
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand

interface Connection : Executor<TerminalCommand> {

    fun getRemote(): Remote

    fun getRemoteOS(): OperatingSystem

    fun getTerminal(): Terminal
}