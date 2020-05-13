package net.milosvasic.factory.mail.component.installer.step.deploy

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.Variable
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand
import java.io.File

open class Deploy(what: String, private val where: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {
        const val delimiter = ":"
        const val prototypePrefix = "proto."
    }

    private val whatFile = File(what)
    private var command = String.EMPTY
    private var terminal: Terminal? = null
    private val operation = DeployOperation()
    private val excludes = listOf("$prototypePrefix*")
    private val localPath = whatFile.parentFile.absolutePath
    private val remoteTar = "$where${File.separator}${whatFile.name}${Commands.tarExtension}"
    protected val localTar = "$localPath${File.separator}${whatFile.name}${Commands.tarExtension}"

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is TerminalCommand -> {
                if (isMkdir(result.operation)) {

                    terminal = connection?.terminal
                    if (terminal == null) {

                        log.e("No terminal for deployment")
                        finish(false, operation)
                    } else {

                        if (whatFile.exists()) {
                            if (whatFile.isDirectory) {

                                try {
                                    processFiles(whatFile)
                                    command = Commands.tar(whatFile.absolutePath, localTar)
                                    terminal?.execute(TerminalCommand(command))
                                } catch (e: IllegalStateException) {

                                    log.e(e)
                                    finish(false, operation)
                                } catch (e: IllegalArgumentException) {

                                    log.e(e)
                                    finish(false, operation)
                                }
                            } else {

                                log.e("${whatFile.absolutePath} is not directory")
                                finish(false, operation)
                            }
                        } else {

                            log.e("File does not exist: ${whatFile.absolutePath}")
                            finish(false, operation)
                        }
                    }
                    return
                }
                if (isTarCompress(result.operation)) {

                    val remote = connection?.getRemote()
                    if (remote == null) {

                        log.e("No remote available")
                        finish(false, operation)
                    } else {

                        command = getScp(remote)
                        try {
                            terminal?.execute(TerminalCommand(command))
                        } catch (e: IllegalArgumentException) {

                            log.e(e)
                            finish(false, operation)
                        } catch (e: IllegalStateException) {

                            log.e(e)
                            finish(false, operation)
                        }
                    }
                    return
                }
                if (isScp(result.operation)) {

                    command = Commands.unTar(remoteTar, where)
                    try {
                        connection?.execute(TerminalCommand(command))
                    } catch (e: IllegalArgumentException) {

                        log.e(e)
                        finish(false, operation)
                    } catch (e: IllegalStateException) {

                        log.e(e)
                        finish(false, operation)
                    }
                    return
                }
                if (isTarDecompress(result.operation)) {

                    command = Commands.rm(remoteTar)
                    try {
                        connection?.execute(TerminalCommand(command))
                    } catch (e: IllegalStateException) {

                        log.e(e)
                        finish(false, operation)
                    } catch (e: IllegalArgumentException) {

                        log.e(e)
                        finish(false, operation)
                    }
                    return
                }
                if (isRmRemote(result.operation, remoteTar)) {

                    var exclude = String.EMPTY
                    excludes.forEach {
                        if (exclude.isNotEmpty() && !exclude.isBlank()) {
                            exclude += ";"
                        }
                        exclude += "${Commands.find(it, Commands.here)} -exec ${Commands.rm} {} \\;"
                    }
                    try {

                        if (exclude != String.EMPTY) {

                            command = exclude
                            connection?.execute(TerminalCommand(command))
                        } else {

                            command = Commands.rm(localTar)
                            terminal?.execute(TerminalCommand(command))
                        }
                    } catch (e: IllegalStateException) {

                        log.e(e)
                        finish(false, operation)
                    } catch (e: IllegalArgumentException) {

                        log.e(e)
                        finish(false, operation)
                    }
                    return
                }
                if (isFind(result.operation)) {

                    command = Commands.rm(localTar)
                    try {
                        terminal?.execute(TerminalCommand(command))
                    } catch (e: IllegalArgumentException) {

                        log.e(e)
                        finish(false, operation)
                    } catch (e: IllegalStateException) {

                        log.e(e)
                        finish(false, operation)
                    }
                    return
                }
                if (isRm(result.operation, localTar)) {

                    val remote = connection?.getRemote()
                    if (remote == null) {

                        log.e("No remote available")
                        finish(false, operation)
                    } else {

                        val chown = Commands.chown(remote.account, where)
                        val chgrp = Commands.chgrp(remote.account, where)
                        val permissions = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)

                        try {
                            val chmod = Commands.chmod(where, permissions.obtain())
                            command = Commands.concatenate(chown, chgrp, chmod)
                            connection?.execute(TerminalCommand(command))
                        } catch (e: IllegalArgumentException) {

                            log.e(e)
                            finish(false, operation)
                        } catch (e: IllegalStateException) {

                            log.e(e)
                            finish(false, operation)
                        }
                    }
                    return
                }
                if (isChown(result.operation) && isChgrp(result.operation) && isChmod(result.operation)) {

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
        command = Commands.mkdir(where)
        connection?.execute(TerminalCommand(command))
    }

    override fun finish(success: Boolean, operation: Operation) {
        cleanupFiles(whatFile)
        super.finish(success, operation)
    }

    protected open fun getScpCommand() = Commands.scp

    protected open fun getScp(remote: Remote) = Commands.scp(localTar, where, remote)

    @Throws(IllegalStateException::class)
    private fun processFiles(directory: File) {
        val fileList = directory.listFiles()
        fileList?.let { files ->
            files.forEach { file ->
                if (file.isDirectory) {
                    processFiles(file)
                } else if (file.name.toLowerCase().startsWith(prototypePrefix)) {
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
                if (file.isDirectory) {
                    cleanupFiles(file)
                } else if (file.name.toLowerCase().startsWith(prototypePrefix)) {
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

    private fun isScp(operation: TerminalCommand) =
            operation.command.startsWith(getScpCommand())

    private fun isTarDecompress(operation: TerminalCommand) =
            operation.command.contains(Commands.tarDecompress)

    private fun isTarCompress(operation: TerminalCommand) =
            operation.command.startsWith(Commands.tarCompress)

    private fun isRmRemote(operation: TerminalCommand, file: String) =
            isRm(operation, file) &&
                    operation.command.startsWith(Commands.ssh)

    private fun isRm(operation: TerminalCommand, file: String) =
            operation.command.contains(Commands.rm(file))

    private fun isFind(operation: TerminalCommand) =
            operation.command.contains(Commands.find)

    private fun isChown(operation: TerminalCommand) =
            operation.command.contains(Commands.chown)

    private fun isChgrp(operation: TerminalCommand) =
            operation.command.contains(Commands.chgrp)

    private fun isChmod(operation: TerminalCommand) =
            operation.command.contains(Commands.chmod)

    private fun isMkdir(operation: TerminalCommand) =
            operation.command.contains(Commands.mkdir)
}