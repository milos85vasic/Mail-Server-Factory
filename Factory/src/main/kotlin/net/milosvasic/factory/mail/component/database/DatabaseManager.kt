package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.Registration
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.validation.Validator

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

    @Throws(IllegalArgumentException::class)
    override fun obtain(vararg param: Type): Database {

        Validator.Arguments.validateSingle(param)
        val type = param[0]
        databases[type]?.let {
            return it
        }
        throw IllegalArgumentException("No database registered for the type: ${type.type}")
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