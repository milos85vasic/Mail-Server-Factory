package net.milosvasic.factory.mail.component.database.command

import net.milosvasic.factory.mail.component.database.Database
import net.milosvasic.factory.mail.component.database.DatabaseConnection

abstract class DatabaseCheckCommand(database: Database, connection: DatabaseConnection) :
        DatabaseCommand(database, connection)