package net.milosvasic.factory.mail.component.database.command

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.database.Database
import net.milosvasic.factory.mail.terminal.TerminalCommand

abstract class DatabaseCommand(val database: Database) : TerminalCommand(String.EMPTY) {

    override val command: String
        get() = getDatabaseCommand()

    abstract fun getDatabaseCommand(): String
}