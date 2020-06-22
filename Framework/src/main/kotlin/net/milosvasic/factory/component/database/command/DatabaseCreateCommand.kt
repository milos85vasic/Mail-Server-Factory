package net.milosvasic.factory.component.database.command

import net.milosvasic.factory.component.database.Database

abstract class DatabaseCreateCommand(database: Database) : DatabaseCommand(database)