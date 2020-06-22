package net.milosvasic.factory.component.docker.step.dockerfile

import net.milosvasic.factory.component.docker.command.BuildImage
import net.milosvasic.factory.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.execution.flow.implementation.CommandFlow

class Build(private val what: String) : DockerInstallationStep() {

    private val validator = BuildValidator()

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {
        connection?.let { conn ->

            val path = getDockerfilePath()
            val name = getName()
            val command = BuildImage(path, name)
            return CommandFlow().width(conn).perform(command)
        }
        throw IllegalArgumentException("No connection provided")
    }

    override fun getOperation() = BuildOperation()

    @Throws(IllegalArgumentException::class)
    private fun getDockerfilePath(): String {

        if (validator.validate(what)) {

            return what.split(":")[0]
        } else {

            throw IllegalArgumentException("Invalid build parameters: $what")
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun getName(): String {

        if (validator.validate(what)) {

            return "${what.split(":")[1]}:${what.split(":")[2]}"
        } else {

            throw IllegalArgumentException("Invalid build parameters: $what")
        }
    }
}