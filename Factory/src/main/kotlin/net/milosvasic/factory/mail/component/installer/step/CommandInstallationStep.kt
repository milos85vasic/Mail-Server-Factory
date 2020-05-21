package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.validation.Validator

open class CommandInstallationStep(val command: TerminalCommand) : InstallationStep<Connection>() {

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {

        Validator.Arguments.validateSingle(params)
        val connection = params[0]
        connection.execute(command)
    }
}