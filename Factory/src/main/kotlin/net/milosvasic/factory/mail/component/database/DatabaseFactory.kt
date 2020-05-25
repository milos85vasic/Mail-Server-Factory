package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.Build
import net.milosvasic.factory.mail.remote.Connection

class DatabaseFactory(
        val type: Type,
        val name: String,
        val connection: Connection

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