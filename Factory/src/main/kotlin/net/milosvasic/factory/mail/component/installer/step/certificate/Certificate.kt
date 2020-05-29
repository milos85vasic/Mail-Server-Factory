package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.remote.ssh.SSH

class Certificate(val name: String) : RemoteOperationInstallationStep<SSH>() {

    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            return CommandFlow()
                    .width(conn)
                    .perform(GeneratePrivateKeyCommand(name))
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = CertificateInitializationOperation()
}