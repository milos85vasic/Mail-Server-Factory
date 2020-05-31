package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.configuration.*
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.command.ChmodCommand
import net.milosvasic.factory.mail.terminal.command.Commands
import net.milosvasic.factory.mail.terminal.command.MkdirCommand

class Certificate(val name: String) : RemoteOperationInstallationStep<SSH>() {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val hostname = conn.getRemoteOS().getHostname()
            val keyHome = VariableKey.CERTIFICATES.key
            val ctxServer = VariableContext.Server.context
            val ctxSeparator = VariableNode.contextSeparator
            val ctxCertification = VariableContext.Certification.context
            val key = "$ctxServer$ctxSeparator$ctxCertification$ctxSeparator$keyHome"
            val configuration = ConfigurationManager.getConfiguration()
            val path = configuration.getVariableParsed(key) as String
            val permission = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)
            val perm = permission.obtain()

            return CommandFlow()
                    .width(conn)
                    .perform(MkdirCommand(path))
                    .perform(GeneratePrivateKeyCommand(path, name))
                    .perform(GenerateRequestKeyCommand(path, Commands.getPrivateKyName(name), name))
                    .perform(ImportRequestKeyCommand(path, Commands.getRequestKeyName(name), hostname))
                    .perform(SignRequestKeyCommand(hostname))
                    .perform(ChmodCommand(path, perm))
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = CertificateInitializationOperation()
}