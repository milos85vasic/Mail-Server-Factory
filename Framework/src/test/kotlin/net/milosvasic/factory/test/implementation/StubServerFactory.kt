package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.application.server_factory.ServerFactory
import net.milosvasic.factory.component.docker.Docker
import net.milosvasic.factory.component.installer.Installer
import net.milosvasic.factory.configuration.ConfigurationFactory
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.command.EchoCommand
import net.milosvasic.factory.terminal.command.UnameCommand

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

    override fun getConfigurationFactory(): ConfigurationFactory<*> {

        return StubServerConfigurationFactory()
    }

    override fun instantiateDocker(ssh: Connection): Docker {

        val docker = super.instantiateDocker(ssh)
        docker.addProcessingRecipesRegistrar(recipeRegistrar)
        return docker
    }

    override fun getHostNameSetCommand(hostname: String) = EchoCommand(hostname)
}