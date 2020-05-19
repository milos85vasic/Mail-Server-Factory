package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.application.server_factory.ServerFactory
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubServerFactory(arguments: List<String> = listOf()) : ServerFactory(arguments) {

    override fun getHostInfoCommand() = TerminalCommand("uname")
}