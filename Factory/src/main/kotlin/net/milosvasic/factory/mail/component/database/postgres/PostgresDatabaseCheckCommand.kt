package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.component.database.command.DatabaseCheckCommand

class PostgresDatabaseCheckCommand(database: Postgres) : DatabaseCheckCommand(database) {

    override fun getDatabaseCommand(): String {

        val connection = database.connection
        return StringBuilder("PGPASSWORD=${connection.password} ${PostgresCommand.PSQL.obtain()}")
                .append(" --host=${connection.host} --port=${connection.port} --user=${connection.user}")
                .append(" --list | grep ${database.name}")
                .toString()
    }
}