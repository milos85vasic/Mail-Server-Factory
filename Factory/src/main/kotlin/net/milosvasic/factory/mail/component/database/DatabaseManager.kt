package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.Registration
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.obtain.ObtainParametrized

object DatabaseManager :
        ObtainParametrized<Type, Database>,
        Registration<Database> {

    private val busy = Busy()
    private val databases = mutableMapOf<Type, Database>()

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun register(what: Database) {
        busy()

    }

    @Throws(IllegalArgumentException::class)
    override fun unregister(what: Database) {
        val type = what.type
        if (databases[type] == what) {
            databases.remove(type)?.terminate()
        } else {

            throw IllegalArgumentException("Database instance is not register: $databases")
        }
    }

    fun unregister(type: Type) {
        databases.remove(type)?.terminate()
    }

    override fun obtain(vararg param: Type): Database {
        TODO("Not yet implemented")
    }

    @Synchronized
    @Throws(BusyException::class)
    private fun busy() {
        BusyWorker.busy(busy)
    }

    @Synchronized
    private fun free() {
        BusyWorker.free(busy)
    }
}