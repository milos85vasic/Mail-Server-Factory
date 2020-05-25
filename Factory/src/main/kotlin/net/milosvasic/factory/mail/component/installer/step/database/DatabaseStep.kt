package net.milosvasic.factory.mail.component.installer.step.database

import net.milosvasic.factory.mail.component.database.DatabaseInitializationOperation
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
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

            val configurationFile = "$path${File.separator}$defaultConfigurationFile"
            return CommandFlow()
                    .width(conn)
                    .perform(TestCommand(configurationFile))
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = DatabaseInitializationOperation()
}