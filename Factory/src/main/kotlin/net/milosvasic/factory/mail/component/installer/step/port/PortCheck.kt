package net.milosvasic.factory.mail.component.installer.step.port

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.command.PortCheckCommand

class PortCheck(private val ports: List<Int>) : RemoteOperationInstallationStep<SSH>() {

    companion object {

        const val delimiter = ","
    }

    @Throws(IllegalArgumentException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val flow = CommandFlow().width(conn)
            ports.forEach {
                flow.perform(PortCheckCommand(it))
            }
            return flow
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = PortCheckOperation()
}