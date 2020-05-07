package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.configuration.VariableContext
import net.milosvasic.factory.mail.configuration.VariableKey
import net.milosvasic.factory.mail.configuration.VariableNode
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand

class Reboot(private val timeoutInSeconds: Int = 120) : RemoteOperationInstallationStep<SSH>() {

    private var pingCount = 0
    private val rebootScheduleTime = 3
    private val defaultCommand = Commands.reboot(rebootScheduleTime)
    private var command = defaultCommand
    private val operation = RebootOperation()
    private var pingCommand: String = String.EMPTY

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is TerminalCommand -> {
                val cmd = result.operation.command
                when {
                    isReboot(cmd) -> {

                        try {
                            Thread.sleep(3000)
                        } catch (e: InterruptedException) {

                            log.e(e)
                            finish(false, operation)
                        }
                        if (result.success) {
                            ping()
                        } else {
                            finish(false, operation)
                        }
                    }
                    isPing(cmd) -> {
                        if (result.success) {
                            finish(true, operation)
                        } else {

                            if (pingCount <= timeoutInSeconds) {
                                ping()
                            } else {

                                log.e("Reboot timeout exceeded")
                                finish(false, operation)
                            }
                        }
                    }
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: SSH) {
        super.execute(*params)
        var rebootAllowed = false
        try {
            val configuration = ConfigurationManager.getConfiguration()
            val rebootKey = "${VariableContext.Server.context}${VariableNode.contextSeparator}${VariableKey.REBOOT_ALLOWED}"
            val rebootValue = configuration.getVariableParsed(rebootKey)
            rebootValue?.let {
                when (it) {
                    is String -> {
                        rebootAllowed = it.toBoolean()
                    }
                    is Boolean -> {
                        rebootAllowed = it
                    }
                    else -> {
                        log.e("Cannot use 'reboot allowed' setting with value of: $it")
                        finish(true, operation)
                    }
                }
            }
        } catch (e: IllegalStateException) {

            log.e(e)
            finish(false, operation)
        }
        if (!rebootAllowed) {
            log.w("Reboot is not allowed by configuration")
            finish(true, operation)
            return
        }
        log.v("Reboot timeout in seconds: $timeoutInSeconds")
        pingCount = 0
        command = defaultCommand
        connection?.execute(TerminalCommand(command))
    }

    private fun ping() {

        pingCount++
        log.v("Ping no. $pingCount")
        val host = connection?.getRemote()?.host
        if (host == null) {

            log.e("No host to ping provided")
            finish(false, operation)
        } else {

            val terminal = connection?.terminal
            if (terminal == null) {

                log.e("No terminal for pinging provided")
                finish(false, operation)
            } else {

                pingCommand = Commands.ping(host, 1)
                command = pingCommand
                try {
                    terminal.execute(TerminalCommand(command))
                } catch (e: IllegalStateException) {

                    log.e(e)
                    finish(false, operation)
                } catch (e: IllegalArgumentException) {

                    log.e(e)
                    finish(false, operation)
                }
            }
        }
    }

    private fun isReboot(cmd: String) = cmd.endsWith(defaultCommand)

    private fun isPing(cmd: String) = pingCommand != String.EMPTY && cmd.endsWith(pingCommand)
}