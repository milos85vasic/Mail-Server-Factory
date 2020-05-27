package net.milosvasic.factory.mail.component.database.command

import net.milosvasic.factory.mail.component.database.Database

abstract class DatabaseSqlCommand(database: Database, protected val sql: String) : DatabaseCommand(database)