package net.milosvasic.factory.mail.component.docker.step.volume

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.docker.DockerCommandBuilder
import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.component.docker.Image
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection


class Volume(private val volumeDefinition: String) : DockerInstallationStep() {

    private var command = String.EMPTY

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is Command -> {
                if (command != String.EMPTY && result.operation.toExecute.endsWith(command)) {

                    finish(result.success, DockerInstallationOperation())
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)
        val volumeParameters = volumeDefinition.split(":")
        val validator = VolumeDefinitionValidator()
        if (validator.validate(*volumeParameters.toTypedArray())) {

            var guest = String.EMPTY
            if (volumeParameters.size > 1) {
                guest = volumeParameters[1].trim()
            }

            val builder = DockerCommandBuilder()
                    .command(DockerCommand.RUN)
                    .image(Image.BUSYBOX.imageName)
                    .volume(volumeParameters[0].trim(), guest)
                    .containerName("TBD") // TODO: Set proper name

            command = builder.build()
            connection?.execute(command)
        } else {

            finish(false, DockerInstallationOperation())
        }
    }
}