package net.milosvasic.factory.component.database.command

import net.milosvasic.factory.component.database.Database

abstract class DatabaseSqlCommand(database: Database, protected val sql: String) : DatabaseCommand(database)