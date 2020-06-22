package net.milosvasic.factory.component.database.postgres

import net.milosvasic.factory.component.database.Database
import net.milosvasic.factory.component.database.command.DatabaseSqlCommand

class PostgresDatabaseSqlCommand(database: Database, sql: String) : DatabaseSqlCommand(database, sql) {

    override fun getDatabaseCommand(): String {

        val connection = database.connection
        return StringBuilder("PGPASSWORD=${connection.password} ${PostgresCommand.PSQL.obtain()}")
                .append(" --host=${connection.host} --port=${connection.port} --user=${connection.user}")
                .append(" ${database.name} < $sql")
                .toString()
    }
}