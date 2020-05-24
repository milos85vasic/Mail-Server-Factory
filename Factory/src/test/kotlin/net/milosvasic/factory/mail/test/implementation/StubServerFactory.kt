package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.application.server_factory.ServerFactory
import net.milosvasic.factory.mail.component.docker.Docker
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.command.EchoCommand
import net.milosvasic.factory.mail.terminal.command.HostNameSetCommand
import net.milosvasic.factory.mail.terminal.command.UnameCommand

class StubServerFactory(arguments: List<String> = listOf()) : ServerFactory(arguments) {

    private val recipeRegistrar = StubRecipeRegistrar()

    override fun getHostInfoCommand() = UnameCommand()

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun instantiateInstaller(ssh: Connection): Installer {

        val installer = super.instantiateInstaller(ssh)
        val stubPackageManager = StubPackageManager(getConnection())

        installer.addSupportedPackageManager(stubPackageManager)
        installer.addProcessingRecipesRegistrar(recipeRegistrar)
        return installer
    }

    override fun instantiateDocker(ssh: Connection): Docker {

        val docker = super.instantiateDocker(ssh)
        docker.addProcessingRecipesRegistrar(recipeRegistrar)
        return docker
    }

    override fun getHostNameSetCommand(hostname: String) = EchoCommand(hostname)
}