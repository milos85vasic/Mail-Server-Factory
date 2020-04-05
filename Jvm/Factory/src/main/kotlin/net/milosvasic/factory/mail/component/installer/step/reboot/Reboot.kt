package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands
import java.util.concurrent.ConcurrentLinkedQueue

class Reboot(private val timeoutInSeconds: Int = 120) :
        InstallationStep<SSH>(), Subscription<OperationResultListener>, Notifying<OperationResult> {

    private val busy = Busy()
    private var pingCount = 0
    private val rebootScheduleTime = 3
    private var connection: SSH? = null
    private val defaultCommand = Commands.reboot(rebootScheduleTime)
    private var command = defaultCommand
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
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
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: SSH) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        BusyWorker.busy(busy)

        log.v("Reboot timeout in seconds: $timeoutInSeconds")
        pingCount = 0
        command = defaultCommand
        connection = params[0]
        if (connection == null) {
            throw IllegalArgumentException("Connection is null.")
        }
        connection?.subscribe(listener)
        connection?.execute(command)
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    @Synchronized
    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }

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

    private fun finish(success: Boolean) {
        connection?.unsubscribe(listener)
        connection = null
        val operation = RebootOperation()
        val operationResult = OperationResult(operation, success)
        notify(operationResult)
    }
}