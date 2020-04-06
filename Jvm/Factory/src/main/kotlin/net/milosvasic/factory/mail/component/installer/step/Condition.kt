package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

class Condition(private val command: String) : InstallationStep<Connection>() {

    private val busy = Busy()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is SSHCommand -> {
                    if (result.operation.command.endsWith(command)) {


                    }
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        BusyWorker.busy(busy)

        val connection = params[0]
        connection.subscribe(listener)
        connection.execute(command)
    }


}