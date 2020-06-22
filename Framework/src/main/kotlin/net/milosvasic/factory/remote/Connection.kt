package net.milosvasic.factory.remote

import net.milosvasic.factory.common.execution.Executor
import net.milosvasic.factory.os.OperatingSystem
import net.milosvasic.factory.terminal.Terminal
import net.milosvasic.factory.terminal.TerminalCommand

interface Connection : Executor<TerminalCommand> {

    fun getRemote(): Remote

    fun getRemoteOS(): OperatingSystem

    fun getTerminal(): Terminal
}