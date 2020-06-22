package net.milosvasic.factory.component.installer.step.port

import net.milosvasic.factory.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.remote.ssh.SSH
import net.milosvasic.factory.terminal.command.PortAvailableCommand
import net.milosvasic.factory.terminal.command.PortTakenCommand

data class PortCheck(
        private val ports: List<Int>,
        private val isAvailable: Boolean

) : RemoteOperationInstallationStep<SSH>() {

    companion object {

        const val delimiter = ","
    }

    @Throws(IllegalArgumentException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val flow = CommandFlow().width(conn)
            ports.forEach {
                if (isAvailable) {
                    flow.perform(PortAvailableCommand(it))
                } else {
                    flow.perform(PortTakenCommand(it))
                }
            }
            return flow
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = PortCheckOperation()
}