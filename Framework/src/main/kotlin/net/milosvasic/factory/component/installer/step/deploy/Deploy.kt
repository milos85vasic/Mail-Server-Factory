package net.milosvasic.factory.component.installer.step.deploy

import net.milosvasic.factory.common.DataHandler
import net.milosvasic.factory.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.configuration.Variable
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.remote.ssh.SSH
import net.milosvasic.factory.security.Permission
import net.milosvasic.factory.security.Permissions
import net.milosvasic.factory.terminal.Terminal
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.*
import java.io.File

open class Deploy(what: String, private val where: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {
        const val delimiter = ":"
        const val prototypePrefix = "proto."
    }

    private val whatFile = File(what)
    private var remote: Remote? = null
    private var terminal: Terminal? = null
    private val excludes = listOf("$prototypePrefix*")
    private val localPath = localPath().absolutePath
    private val remoteTar = "$where${File.separator}${whatFile.name}${Commands.tarExtension}"
    protected val localTar = "$localPath${File.separator}${whatFile.name}${Commands.tarExtension}"

    private val onDirectoryCreated = object : DataHandler<OperationResult> {
        override fun onData(data: OperationResult?) {

            if (data == null || !data.success) {

                finish(false)
                return
            }
            if (whatFile.exists()) {
                if (whatFile.isDirectory) {
                    try {

                        processFiles(whatFile)
                    } catch (e: IllegalStateException) {

                        log.e(e)
                        finish(false)
                    } catch (e: IllegalArgumentException) {

                        log.e(e)
                        finish(false)
                    }
                } else {

                    log.e("${whatFile.absolutePath} is not directory")
                    finish(false)
                }
            } else {
                log.e("File does not exist: ${whatFile.absolutePath}")
                finish(false)
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->
            terminal = conn.getTerminal()
            remote = connection?.getRemote()

            terminal?.let { term ->
                remote?.let { rmt ->

                    val flow = CommandFlow()
                            .width(conn)
                            .perform(MkdirCommand(where), onDirectoryCreated)
                            .width(term)
                            .perform(TarCommand(whatFile.absolutePath, localTar))
                            .perform(getScp(rmt))
                            .width(conn)
                            .perform(UnTarCommand(remoteTar, where))
                            .perform(RmCommand(remoteTar))

                    try {
                        val protoCleanup = getProtoCleanup()
                        flow.perform(protoCleanup)
                    } catch (e: IllegalArgumentException) {

                        log.w(e)
                    }

                    return flow
                            .width(term)
                            .perform(RmCommand(localTar))
                            .width(conn)
                            .perform(getSecurityChanges(rmt))
                }
            }
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun finish(success: Boolean) {
        try {

            cleanupFiles(whatFile)
            super.finish(success)
        } catch (e: IllegalStateException) {

            log.e(e)
            super.finish(false)
        }
    }

    override fun getOperation() = DeployOperation()

    protected open fun getScpCommand() = Commands.scp

    protected open fun getScp(remote: Remote): TerminalCommand = ScpCommand(localTar, where, remote)

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

    private fun localPath(): File {
        whatFile.parentFile?.let {
            return it
        }
        return whatFile
    }

    @Throws(IllegalArgumentException::class)
    protected open fun getProtoCleanup(): TerminalCommand {

        if (excludes.isEmpty()) {
            throw IllegalArgumentException("No excludes available")
        }
        val excluded = mutableListOf<String>()
        excludes.forEach {
            val exclude = FindAndRemoveCommand(it, Commands.here)
            excluded.add(exclude.command)
        }
        return ConcatenateCommand(*excluded.toTypedArray())
    }

    protected open fun getSecurityChanges(remote: Remote): TerminalCommand {

        val chown = Commands.chown(remote.account, where)
        val chgrp = Commands.chgrp(remote.account, where)
        val permissions = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)
        val chmod = Commands.chmod(where, permissions.obtain())
        return ConcatenateCommand(chown, chgrp, chmod)
    }
}