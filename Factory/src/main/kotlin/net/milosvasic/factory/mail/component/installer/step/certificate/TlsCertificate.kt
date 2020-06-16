package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.configuration.*
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.command.*
import java.io.File

class TlsCertificate(name: String) : Certificate(name) {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {

        connection?.let { conn ->

            val sep = File.separator
            val subject = Commands.getOpensslSubject()
            val hostname = conn.getRemoteOS().getHostname()
            val certificates = "{{SERVER.CERTIFICATION.CERTIFICATES}}"
            val certificatesPath = Variable.parse(certificates)
            val passIn = "-passin pass:{{SERVER.CERTIFICATION.PASSPHRASE}}"
            val passOut = "-passout pass:{{SERVER.CERTIFICATION.PASSPHRASE}}"
            val permission600 = Permissions(Permission(6), Permission.NONE, Permission.NONE).obtain()

            val crtVerificationCommand = TestCommand("$certificatesPath$sep$hostname.crt")
            val keyVerificationCommand = TestCommand("$certificatesPath$sep$hostname.key")
            val caVerificationCommand = TestCommand("$certificatesPath${sep}ca-bundle.crt")
            val installation = ConcatenateCommand(
                    Commands.cd(certificatesPath),
                    Commands.openssl("genrsa $passOut -aes128 2048 > $hostname.key"),
                    Commands.openssl("rsa $passIn -in $hostname.key -out $hostname.key"),
                    Commands.openssl("req -subj $subject -utf8 -new -key $hostname.key -out $hostname.csr"),
                    Commands.openssl("x509 -in $hostname.csr -out $hostname.crt -req -signkey $hostname.key -days 3650"),
                    Commands.chmod("$hostname.key", permission600)
            )

            val toolkit = Toolkit(conn)
            val checkFlow = InstallationStepFlow(toolkit)
                    .registerRecipe(SkipCondition::class, ConditionRecipe::class)
                    .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                    .width(CommandInstallationStep(caVerificationCommand))
                    .width(SkipCondition(crtVerificationCommand))
                    .width(SkipCondition(keyVerificationCommand))


            val completionFlow = CommandFlow()
                    .width(conn)
                    .perform(caVerificationCommand)
                    .perform(keyVerificationCommand)
                    .perform(crtVerificationCommand)

            return CommandFlow()
                    .width(conn)
                    .perform(TestCommand(certificatesPath))
                    .perform(installation)
                    .connect(checkFlow)
                    .connect(completionFlow)
        }
        throw IllegalArgumentException("No proper connection provided")
    }
}