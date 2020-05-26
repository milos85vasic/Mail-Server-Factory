package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.component.database.command.DatabaseCheckCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand

class PostgresDatabaseCheckCommand(database: Postgres) : DatabaseCheckCommand(database) {

    override fun getCommand(
            connection: DatabaseConnection
    ): TerminalCommand {

        return PostgresDatabaseCheckTerminalCommand(database, connection)
    }
}