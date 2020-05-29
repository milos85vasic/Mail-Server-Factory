package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.*
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.remote.ssh.SSH

class Certificate(val name: String) : RemoteOperationInstallationStep<SSH>() {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val keyHome = VariableKey.CERTIFICATES.key
            val ctxServer = VariableContext.Server.context
            val ctxSeparator = VariableNode.contextSeparator
            val ctxCertification = VariableContext.Certification.context
            val key = "$ctxServer$ctxSeparator$ctxCertification$ctxSeparator$keyHome"
            val configuration = ConfigurationManager.getConfiguration()
            val path = Variable.parse("${configuration.getVariableParsed(key)}") // TODO: Improve parsing!

            return CommandFlow()
                    .width(conn)
                    .perform(GeneratePrivateKeyCommand(path, name))
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = CertificateInitializationOperation()
}