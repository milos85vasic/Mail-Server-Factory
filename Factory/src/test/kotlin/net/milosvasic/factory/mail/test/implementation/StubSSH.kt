package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubSSH(
        var extension: String = defaultExtension()
) : SSH(
        Remote(host = "stub", port = 0, account = "stub")
) {

    companion object {

        fun defaultExtension() = " && echo '${stubCommandMarker}'"

        val stubCommandMarker = StubSSH::class.simpleName.toString()
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(what: TerminalCommand) {
        val command = TerminalCommand("${what.command}$extension", what.configuration)
        terminal.execute(command)
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(data: TerminalCommand, obtainOutput: Boolean) {
        val command = TerminalCommand("${data.command}$extension")
        command.configuration[CommandConfiguration.OBTAIN_RESULT] = obtainOutput
        terminal.execute(command)
    }
}