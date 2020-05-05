package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand

class Reboot(private val timeoutInSeconds: Int = 120) : RemoteOperationInstallationStep<SSH>() {

    private var pingCount = 0
    private val rebootScheduleTime = 3
    private val operation = RebootOperation()
    private val defaultCommand = Commands.reboot(rebootScheduleTime)
    private var command = defaultCommand

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is TerminalCommand -> {
                if (result.operation.command.endsWith(command)) {
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
            }
            is Command -> {

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

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: SSH) {
        super.execute(*params)
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

                command = Commands.ping(host, 1)
                terminal.execute(TerminalCommand(command))
            }
        }
    }
}