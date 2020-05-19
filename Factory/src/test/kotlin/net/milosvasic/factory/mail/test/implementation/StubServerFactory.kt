package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.application.server_factory.ServerFactory
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubServerFactory(arguments: List<String> = listOf()) : ServerFactory(arguments) {

    override fun getHostInfoCommand() = TerminalCommand("uname")

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun instantiateInstaller(ssh: Connection): Installer {
        val installer = super.instantiateInstaller(ssh)
        val stubPackageManager = StubPackageManager(getConnection())
        installer.addSupportedPackageManager(stubPackageManager)
        return installer
    }
}