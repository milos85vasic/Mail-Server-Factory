package net.milosvasic.factory.component.docker.step.network

import net.milosvasic.factory.component.docker.command.NetworkCreate
import net.milosvasic.factory.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.execution.flow.implementation.CommandFlow

class Network(private val name: String) : DockerInstallationStep() {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            return CommandFlow()
                    .width(conn)
                    .perform(NetworkCreate(name))
        }
        throw IllegalArgumentException("No connection provided")
    }

    override fun getOperation() = NetworkSetupOperation()

}