package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands

class Reboot(private val timeoutInSeconds: Int = 120) : InstallationStep<Connection>() {

    private val busy = Busy()
    private val command = Commands.reboot()
    private var connection: Connection? = null

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is SSHCommand -> {
                    if (result.operation.command.endsWith(command)) {

                        connection?.unsubscribe(this)
                        if (result.success) {

                            // TODO: Ping until success.
                        } else {

                            // TODO: Notify on failure.
                        }
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

        log.v("Reboot timeout in seconds: $timeoutInSeconds")
        connection = params[0]
        if (connection == null) {
            throw IllegalArgumentException("Connection is null.")
        }
        connection?.subscribe(listener)
        connection?.execute(command)
    }
}