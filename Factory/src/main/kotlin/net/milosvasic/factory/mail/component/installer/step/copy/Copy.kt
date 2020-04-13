package net.milosvasic.factory.mail.component.installer.step.copy

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import java.io.File

class Copy(what: String, private val where: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {
        const val delimiter = ":"
    }

    private val whatFile = File(what)
    private var command = String.EMPTY
    private var terminal: Terminal? = null
    private val operation = CopyOperation()
    private val destinationPath = whatFile.parentFile.absolutePath
    private val destination = "$destinationPath${File.separator}${whatFile.name}${Commands.tarExtension}"

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is Command -> {
                if (result.operation.toExecute.startsWith(Commands.tarCompress)) {

                    val remote = connection?.getRemote()
                    if (remote == null) {

                        log.e("No remote available")
                        finish(false, operation)
                    } else {

                        command = Commands.scp(destination, where, remote)
                        terminal?.execute(Command(command))
                    }
                    return
                }
                if (result.operation.toExecute.startsWith(Commands.scp)) {

                    val file = "$where${File.separator}${whatFile.name}${Commands.tarExtension}"
                    command = Commands.unTar(file, where)
                    connection?.execute(command)
                    return
                }
                if (result.operation.toExecute.contains(Commands.tarDecompress)) {

                    finish(result.success, operation)
                    return
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: SSH) {
        super.execute(*params)
        terminal = connection?.terminal
        if (terminal == null) {

            log.e("No terminal for copying")
            finish(false, operation)
        } else {

            if (whatFile.exists()) {
                if (whatFile.isDirectory) {

                    command = Commands.tar(whatFile.absolutePath, destination)
                    terminal?.execute(Command(command))
                } else {

                    log.e("${whatFile.absolutePath} is not directory")
                    finish(false, operation)
                }
            } else {

                log.e("File does not exist: ${whatFile.absolutePath}")
                finish(false, operation)
            }
        }
    }
}