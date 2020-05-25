package net.milosvasic.factory.mail.component.installer.step.database

import net.milosvasic.factory.mail.component.database.*
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.RegistrationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.command.TestCommand
import java.io.File

class DatabaseStep(val path: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {

        const val defaultConfigurationFile = "configuration.json"
    }

    @Throws(IllegalArgumentException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val manager = DatabaseManager
            val configurationFile = "$path${File.separator}$defaultConfigurationFile"

            // TODO: Instantiate registration
            val registration = DatabaseRegistration(
                    Postgres("Test", conn),
                    object : OperationResultListener {
                        override fun onOperationPerformed(result: OperationResult) {

                            log.e("> > > > > > TODO: $result")
                        }
                    }
            )

            val registrationFlow = RegistrationFlow<DatabaseRegistration>()
                    .width(manager)
                    .perform(registration)

            return CommandFlow()
                    .width(conn)
                    .perform(TestCommand(configurationFile))
                    .connect(registrationFlow)
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = DatabaseInitializationOperation()
}