package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.component.database.command.DatabaseCheckCommand

class PostgresDatabaseCheckCommand(database: Postgres, connection: DatabaseConnection) :
        DatabaseCheckCommand(database, connection) {

    override fun getDatabaseCommand() =
            StringBuilder("PGPASSWORD=${connection.password} psql --host=${connection.host}")
                    .append(" --port=${connection.port} --user=${connection.user}")
                    .append(" --list | grep ${database.name}")
                    .toString()
}