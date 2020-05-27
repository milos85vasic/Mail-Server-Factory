package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.component.database.command.DatabaseCreateCommand

class PostgresDatabaseCreateCommand (database: Postgres, connection: DatabaseConnection) :
        DatabaseCreateCommand(database, connection) {

    override fun getDatabaseCommand() =
        StringBuilder("PGPASSWORD=${connection.password} ${PostgresCommand.PSQL.obtain()}")
                .append(" --host=${connection.host} --port=${connection.port} --user=${connection.user}")
                .append(" -c 'CREATE DATABASE ${database.name};'")
                .toString()
}