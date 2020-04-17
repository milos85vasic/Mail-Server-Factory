package net.milosvasic.factory.mail.component.installer.step.deploy

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.Variable
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import java.io.File

class Deploy(what: String, private val where: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {
        const val delimiter = ":"
        const val prototypePrefix = "proto."
    }

    private val whatFile = File(what)
    private var command = String.EMPTY
    private var terminal: Terminal? = null
    private val operation = DeployOperation()
    private val localPath = whatFile.parentFile.absolutePath
    private val localTar = "$localPath${File.separator}${whatFile.name}${Commands.tarExtension}"
    private val remoteTar = "$where${File.separator}${whatFile.name}${Commands.tarExtension}"

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is Command -> {
                if (isTarCompress(result.operation)) {

                    val remote = connection?.getRemote()
                    if (remote == null) {

                        log.e("No remote available")
                        finish(false, operation)
                    } else {

                        command = Commands.scp(localTar, where, remote)
                        terminal?.execute(Command(command))
                    }
                    return
                }
                if (isScp(result.operation)) {

                    command = Commands.unTar(remoteTar, where)
                    connection?.execute(command)
                    return
                }
                if (isTarDecompress(result.operation)) {

                    command = Commands.rm(remoteTar)
                    connection?.execute(command)
                    return
                }
                if (isRmRemote(result.operation, remoteTar)) {

                    command = Commands.rm(localTar)
                    terminal?.execute(Command(command))
                    return
                }
                if (isRm(result.operation, localTar)) {

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

            log.e("No terminal for deployment")
            finish(false, operation)
        } else {

            if (whatFile.exists()) {
                if (whatFile.isDirectory) {

                    processFiles(whatFile)
                    // TODO: Tar all except proto files.
                    command = Commands.tar(whatFile.absolutePath, localTar)
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

    override fun finish(success: Boolean, operation: Operation) {
        cleanupFiles(whatFile)
        super.finish(success, operation)
    }

    @Throws(IllegalStateException::class)
    private fun processFiles(directory: File) {
        val fileList = directory.listFiles()
        fileList?.let { files ->
            files.forEach { file ->
                if (file.name.toLowerCase().startsWith(prototypePrefix)) {
                    processFile(directory, file)
                }
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun cleanupFiles(directory: File) {
        val fileList = directory.listFiles()
        fileList?.let { files ->
            files.forEach { file ->
                if (file.name.toLowerCase().startsWith(prototypePrefix)) {
                    val toRemove = File(directory, getName(file))
                    cleanupFile(toRemove)
                }
            }
        }
    }

    private fun cleanupFile(file: File) {
        if (file.exists()) {
            if (file.delete()) {
                log.v("File is removed: ${file.absolutePath}")
            } else {
                log.w("File could not be removed: ${file.absolutePath}")
            }
        } else {
            log.w("File does not exist: ${file.absolutePath}")
        }
    }

    @Throws(IllegalStateException::class)
    private fun processFile(directory: File, file: File) {
        if (!file.exists()) {
            throw IllegalStateException("File does not exist: ${file.absolutePath}")
        }
        log.v("Processing prototype file: ${file.absolutePath}")
        val content = file.readText()
        if (content.isNotEmpty() && !content.isBlank()) {
            val parsedContent = Variable.parse(content)
            val destination = File(directory, getName(file))
            if (destination.exists()) {
                throw IllegalStateException("Destination file already exist: ${destination.absolutePath}")
            } else {
                if (destination.createNewFile()) {
                    destination.writeText(parsedContent)
                } else {
                    throw IllegalStateException("Can't create destination file: ${destination.absolutePath}")
                }
            }
        }
    }

    private fun getName(file: File) = file.name.toLowerCase().replace(prototypePrefix, "")

    private fun isScp(operation: Command) =
            operation.toExecute.startsWith(Commands.scp)

    private fun isTarDecompress(operation: Command) =
            operation.toExecute.contains(Commands.tarDecompress)

    private fun isTarCompress(operation: Command) =
            operation.toExecute.startsWith(Commands.tarCompress)

    private fun isRmRemote(operation: Command, file: String) =
            isRm(operation, file) &&
                    operation.toExecute.startsWith(Commands.ssh)

    private fun isRm(operation: Command, file: String) =
            operation.toExecute.contains(Commands.rm(file))
}