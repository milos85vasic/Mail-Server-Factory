package net.milosvasic.factory.component.installer.step.reboot

import net.milosvasic.factory.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.configuration.ConfigurationManager
import net.milosvasic.factory.configuration.VariableContext
import net.milosvasic.factory.configuration.VariableKey
import net.milosvasic.factory.configuration.VariableNode
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.remote.ssh.SSH
import net.milosvasic.factory.terminal.Terminal
import net.milosvasic.factory.terminal.command.EchoCommand
import net.milosvasic.factory.terminal.command.PingCommand
import net.milosvasic.factory.terminal.command.RebootCommand

class Reboot(private val timeoutInSeconds: Int = 120) : RemoteOperationInstallationStep<SSH>() {

    private var pingCount = 0
    private val rebootScheduleTime = 3
    private var remote: Remote? = null
    private var terminal: Terminal? = null

    private val pingCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is PingCommand -> {
                    if (result.success) {
                        finish(true)
                    } else {

                        if (pingCount <= timeoutInSeconds) {
                            ping()
                        } else {

                            log.e("Reboot timeout exceeded")
                            finish(false)
                        }
                    }
                }
            }
        }
    }

    override fun getFlow(): CommandFlow {
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
                        finish(true)
                    }
                }
            }
        } catch (e: IllegalStateException) {

            log.e(e)
            finish(false)
        }

        connection?.let { conn ->
            terminal = conn.getTerminal()
            remote = conn.getRemote()

            terminal?.let { term ->
                remote?.let { _ ->

                    return if (!rebootAllowed) {

                        CommandFlow()
                                .width(term)
                                .perform(EchoCommand("Reboot is not allowed by configuration"))
                    } else {

                        log.v("Reboot timeout in seconds: $timeoutInSeconds")
                        pingCount = 0
                        term.subscribe(pingCallback)

                        CommandFlow()
                                .width(conn)
                                .perform(RebootCommand(rebootScheduleTime))
                    }
                }
            }
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = RebootOperation()

    override fun finish(success: Boolean) {
        if (success && pingCount == 0) {

            try {
                Thread.sleep(3000)
                ping()
            } catch (e: InterruptedException) {

                log.e(e)
                finish(false)
            }
        } else {

            terminal?.unsubscribe(pingCallback)
            super.finish(success)
        }
    }

    private fun ping() {

        pingCount++
        log.v("Ping no. $pingCount")
        val host = remote?.host
        if (host == null) {

            log.e("No host to ping provided")
            finish(false)
        } else {

            terminal?.let { term ->
                try {
                    term.execute(PingCommand(host, 1))
                } catch (e: IllegalStateException) {

                    log.e(e)
                    finish(false)
                } catch (e: IllegalArgumentException) {

                    log.e(e)
                    finish(false)
                }
            }
        }
    }
}