package net.milosvasic.factory.component.database.postgres

import net.milosvasic.factory.component.database.command.DatabaseCreateCommand

class PostgresDatabaseCreateCommand(database: Postgres) :
        DatabaseCreateCommand(database) {

    override fun getDatabaseCommand(): String {

        val connection = database.connection
        return StringBuilder("PGPASSWORD=${connection.password} ${PostgresCommand.PSQL.obtain()}")
                .append(" --host=${connection.host} --port=${connection.port} --user=${connection.user}")
                .append(" -c 'CREATE DATABASE ${database.name};'")
                .toString()
    }
}