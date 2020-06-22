package net.milosvasic.factory.component.database

import net.milosvasic.factory.common.Build
import net.milosvasic.factory.component.database.postgres.Postgres

class DatabaseFactory(
        val type: Type,
        val name: String,
        val connection: DatabaseConnection

) : Build<Database> {

    @Throws(IllegalArgumentException::class)
    override fun build(): Database {
        when (type) {
            Type.Postgres -> {
                return Postgres(name, connection)
            }
            else -> {
                throw IllegalArgumentException("Unsupported database type: ${type.type}")
            }
        }
    }
}