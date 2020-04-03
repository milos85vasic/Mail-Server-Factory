package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.remote.Connection

class CommandInstallationStep(val command: String) : InstallationStep<Connection>() {

    @Synchronized
    @Throws(IllegalArgumentException::class)
    override fun execute(vararg params: Connection) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        val connection = params[0]
        connection.execute(command)
    }
}