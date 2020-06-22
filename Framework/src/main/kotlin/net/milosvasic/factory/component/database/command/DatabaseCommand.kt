package net.milosvasic.factory.component.database.command

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.component.database.Database
import net.milosvasic.factory.terminal.TerminalCommand

abstract class DatabaseCommand(val database: Database) : TerminalCommand(String.EMPTY) {

    override val command: String
        get() = getDatabaseCommand()

    abstract fun getDatabaseCommand(): String
}