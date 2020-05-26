package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.component.database.Database
import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.component.database.command.DatabaseTerminalCommand

class PostgresDatabaseCheckTerminalCommand(
        database: Database,
        connection: DatabaseConnection

) : DatabaseTerminalCommand(

        "PGPASSWORD=${connection.password} psql --host=${connection.host} --port=${connection.port} --user=${connection.user} --list | grep ${database.name}"
)