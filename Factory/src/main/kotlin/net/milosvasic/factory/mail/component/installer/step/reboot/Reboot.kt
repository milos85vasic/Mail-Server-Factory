package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands

class Reboot(private val timeoutInSeconds: Int = 120) : RemoteOperationInstallationStep<SSH>() {

    private var pingCount = 0
    private val rebootScheduleTime = 3
    private val defaultCommand = Commands.reboot(rebootScheduleTime)
    private var command = defaultCommand

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is SSHCommand -> {
                if (result.operation.command.endsWith(command)) {
                    try {
                        Thread.sleep(3000)
                    } catch (e: InterruptedException) {

                        log.e(e)
                        finish(false)
                    }
                    if (result.success) {
                        ping()
                    } else {
                        finish(false)
                    }
                }
            }
            is Command -> {

                if (result.success) {
                    finish(true)
                } else {

                    if (pingCount <= timeoutInSeconds) {
                        ping()
                    } else {

                        log.e("Reboot timeout exceeded.")
                        finish(false)
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
        connection?.execute(command)
    }

    override fun getOperation() = RebootOperation()

    private fun ping() {

        pingCount++
        log.v("Ping no. $pingCount")
        val host = connection?.getRemote()?.host
        if (host == null) {

            log.e("No host to ping provided")
            finish(false)
        } else {

            val terminal = connection?.terminal
            if (terminal == null) {

                log.e("No terminal for pinging provided")
                finish(false)
            } else {

                command = Commands.ping(host, 1)
                terminal.execute(Command(command))
            }
        }
    }
}