package net.milosvasic.factory.component.installer.step

import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.validation.Validator

open class CommandInstallationStep(val command: TerminalCommand) : InstallationStep<Connection>() {

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {

        Validator.Arguments.validateSingle(params)
        val connection = params[0]
        connection.execute(command)
    }
}