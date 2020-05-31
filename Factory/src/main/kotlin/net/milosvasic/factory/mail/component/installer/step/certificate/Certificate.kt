package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.configuration.*
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.command.ChmodCommand
import net.milosvasic.factory.mail.terminal.command.Commands
import net.milosvasic.factory.mail.terminal.command.MkdirCommand
import net.milosvasic.factory.mail.terminal.command.TestCommand
import java.io.File

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
            val sep = File.separator
            val certificateExtension = ".crt"
            val issued = "${sep}pki${sep}issued$sep"
            val verificationPath = "{{SERVER.CERTIFICATION.HOME}}$issued$hostname$certificateExtension"
            val verificationCommand = TestCommand(Variable.parse(verificationPath))

            val toolkit = Toolkit(conn)
            val checkFlow = InstallationStepFlow(toolkit)
                    .registerRecipe(SkipCondition::class, ConditionRecipe::class)
                    .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                    .width(SkipCondition(verificationCommand))
                    .width(CommandInstallationStep(GeneratePrivateKeyCommand(path, name)))
                    .width(CommandInstallationStep(GenerateRequestKeyCommand(path, Commands.getPrivateKyName(name), name)))
                    .width(CommandInstallationStep(ImportRequestKeyCommand(path, Commands.getRequestKeyName(name), hostname)))
                    .width(CommandInstallationStep(SignRequestKeyCommand(hostname)))
                    .width(CommandInstallationStep(ChmodCommand(path, perm)))

            val completionFlow = CommandFlow()
                    .width(conn)
                    .perform(verificationCommand)

            return CommandFlow()
                    .width(conn)
                    .perform(MkdirCommand(path))
                    .connect(checkFlow)
                    .connect(completionFlow)
        }
        throw IllegalArgumentException("No proper connection provided")
    }

    override fun getOperation() = CertificateInitializationOperation()
}