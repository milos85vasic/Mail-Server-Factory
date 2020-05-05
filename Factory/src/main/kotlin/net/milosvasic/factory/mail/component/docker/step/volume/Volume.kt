package net.milosvasic.factory.mail.component.docker.step.volume

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.docker.DockerCommandBuilder
import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.component.docker.Image
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand


class Volume(private val mapping: String, private val name: String) : DockerInstallationStep() {

    private val psA = "${DockerCommand.DOCKER.obtain()} ${DockerCommand.PS.obtain()} -a"
    private var command = "$psA | ${Commands.grep(name)}"

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is TerminalCommand -> {
                if (result.operation.command.contains(psA)) {

                    if (result.success) {
                        log.w("Volume '$name' already exist, skipping installation step")
                        finish(result.success, DockerInstallationOperation())
                    } else {

                        val volumeParameters = mapping.split(":")
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
                                    .containerName(name)

                            command = builder.build()
                            try {
                                connection?.execute(TerminalCommand(command))
                            } catch (e: IllegalArgumentException) {

                                log.e(e)
                                finish(false, DockerInstallationOperation())
                            }  catch (e: IllegalStateException) {

                                log.e(e)
                                finish(false, DockerInstallationOperation())
                            }
                        } else {

                            finish(false, DockerInstallationOperation())
                        }
                    }
                    return
                }
                if (command != String.EMPTY && result.operation.command.endsWith(command)) {

                    finish(result.success, DockerInstallationOperation())
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)
        connection?.execute(TerminalCommand(command))
    }
}