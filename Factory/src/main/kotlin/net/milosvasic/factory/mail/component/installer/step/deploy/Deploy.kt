package net.milosvasic.factory.mail.component.installer.step.deploy

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.Variable
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.log
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
    private var remote: Remote? = null
    private var terminal: Terminal? = null
    private val excludes = listOf("$prototypePrefix*")
    private val localPath = localPath().absolutePath
    private val remoteTar = "$where${File.separator}${whatFile.name}${Commands.tarExtension}"
    protected val localTar = "$localPath${File.separator}${whatFile.name}${Commands.tarExtension}"

    private val onDirectoryCreated = object : DataHandler<String> {
        override fun onData(data: String?) {

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

    override fun getFlow(): CommandFlow {

        connection?.let { conn ->
            terminal = conn.terminal
            remote = connection?.getRemote()

            terminal?.let { term ->
                remote?.let { rmt ->

                    return CommandFlow()
                            .width(conn)
                            .perform(Commands.mkdir(where), onDirectoryCreated)
                            .width(term)
                            .perform(Commands.tar(whatFile.absolutePath, localTar))
                            .perform(getScp(rmt))
                            .width(conn)
                            .perform(Commands.unTar(remoteTar, where))
                            .perform(Commands.rm(remoteTar))
                            .perform(getExclude())
                            .width(term)
                            .perform(Commands.rm(localTar))
                            .width(conn)
                            .perform(getPermissionsChanges(rmt))
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

    protected open fun getScp(remote: Remote) = Commands.scp(localTar, where, remote)

    protected open fun isRemote(operation: TerminalCommand) =
            operation.command.startsWith(Commands.ssh)

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

    private fun getExclude(): String {

        var exclude = String.EMPTY
        excludes.forEach {
            if (exclude.isNotEmpty() && !exclude.isBlank()) {
                exclude += ";"
            }
            exclude += "${Commands.find(it, Commands.here)} -exec ${Commands.rm} {} \\;"
        }
        return exclude
    }

    fun getPermissionsChanges(remote: Remote): String {

        val chown = Commands.chown(remote.account, where)
        val chgrp = Commands.chgrp(remote.account, where)
        val permissions = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)
        val chmod = Commands.chmod(where, permissions.obtain())
        return Commands.concatenate(chown, chgrp, chmod)
    }
}