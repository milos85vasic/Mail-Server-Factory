package net.milosvasic.factory.mail.component.database.command

import net.milosvasic.factory.mail.component.database.Database

abstract class DatabaseCreateCommand(database: Database) : DatabaseCommand(database)