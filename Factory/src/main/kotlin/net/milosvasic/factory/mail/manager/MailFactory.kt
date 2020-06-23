package net.milosvasic.factory.mail.manager

import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.command.EchoCommand

class MailFactory(private val connection: Connection) {

    fun getMailCreationFlow(): CommandFlow {

        return CommandFlow()
                .width(connection)
                .perform(EchoCommand("We are about to create email accounts"))
    }
}