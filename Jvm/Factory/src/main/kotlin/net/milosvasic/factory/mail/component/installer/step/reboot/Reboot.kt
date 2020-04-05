package net.milosvasic.factory.mail.component.installer.step.reboot

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

    private val command = Commands.reboot()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is SSHCommand -> {

                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class)
    override fun execute(vararg params: Connection) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        val connection = params[0]

        log.v("Reboot timeout in seconds: $timeoutInSeconds")
        connection.execute(command)

        // TODO: Execute and send RebootOperation
    }
}