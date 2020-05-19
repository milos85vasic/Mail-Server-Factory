package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.localhost
import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubSSH : SSH(
        Remote(host = localhost, port = 0, account = System.getProperty("user.name"))
) {

    companion object {

        const val stubCommandMarker: String = "\t\t\t\t\t"
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(what: TerminalCommand) {
        val command = TerminalCommand("${what.command}$stubCommandMarker", what.configuration)
        getTerminal().execute(command)
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(data: TerminalCommand, obtainOutput: Boolean) {
        val command = TerminalCommand("${data.command}$stubCommandMarker")
        command.configuration[CommandConfiguration.OBTAIN_RESULT] = obtainOutput
        getTerminal().execute(command)
    }
}