package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.configuration.VariableContext
import net.milosvasic.factory.mail.configuration.VariableKey
import net.milosvasic.factory.mail.configuration.VariableNode
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand

class Reboot(private val timeoutInSeconds: Int = 120) : RemoteOperationInstallationStep<SSH>() {

    private var pingCount = 0
    private val rebootScheduleTime = 3
    private var remote: Remote? = null
    private var terminal: Terminal? = null
    private var pingCommand: String = String.EMPTY

    private val pingCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is TerminalCommand -> {
                    val cmd = result.operation.command
                    when {
                        isPing(cmd) -> {
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
            terminal = conn.terminal
            remote = conn.getRemote()

            terminal?.let { term ->
                remote?.let { _ ->

                    return if (!rebootAllowed) {

                        CommandFlow()
                                .width(term)
                                .perform(Commands.echo("Reboot is not allowed by configuration"))
                    } else {

                        log.v("Reboot timeout in seconds: $timeoutInSeconds")
                        pingCount = 0
                        term.subscribe(pingCallback)

                        CommandFlow()
                                .width(conn)
                                .perform(Commands.reboot(rebootScheduleTime))
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

    private fun isPing(cmd: String) = pingCommand != String.EMPTY && cmd.endsWith(pingCommand)

    private fun ping() {

        pingCount++
        log.v("Ping no. $pingCount")
        val host = remote?.host
        if (host == null) {

            log.e("No host to ping provided")
            finish(false)
        } else {

            terminal?.let { term ->
                pingCommand = Commands.ping(host, 1)
                try {
                    term.execute(TerminalCommand(pingCommand))
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