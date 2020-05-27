package net.milosvasic.factory.mail.component.database.command

import net.milosvasic.factory.mail.component.database.Database
import net.milosvasic.factory.mail.component.database.postgres.PostgresCommand

class DatabaseSqlCommand(database: Database, val sql: String) : DatabaseCommand(database) {

    override fun getDatabaseCommand(): String {

        val connection = database.connection
        return StringBuilder("PGPASSWORD=${connection.password} ${PostgresCommand.PSQL.obtain()}")
                .append(" --host=${connection.host} --port=${connection.port} --user=${connection.user}")
                .append(" ${database.name} < $sql")
                .toString()
    }
}