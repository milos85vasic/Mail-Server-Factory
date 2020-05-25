package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.Registration
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.initialization.Termination
import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.validation.Validator

object DatabaseManager :
        ObtainParametrized<Type, Database>,
        Registration<DatabaseRegistration>,
        Termination {

    private val busy = Busy()
    private val databases = mutableMapOf<Type, Database>()

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun register(what: DatabaseRegistration) {
        busy()

    }

    @Throws(IllegalArgumentException::class)
    override fun unRegister(what: DatabaseRegistration) {
        val type = what.database.type
        if (databases[type] == what.database) {
            databases.remove(type)?.terminate()

            val result = OperationResult(DatabaseOperation(), true)
            what.callback.onOperationPerformed(result)
        } else {

            log.e("Database instance is not registered: $databases")
            val result = OperationResult(DatabaseOperation(), false)
            what.callback.onOperationPerformed(result)
        }
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
    @Throws(IllegalStateException::class)
    override fun terminate() {
        busy()
        databases.keys.forEach { key ->
            unRegister(key)
        }
        free()
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

    private fun unRegister(type: Type) {
        databases.remove(type)?.terminate()
    }
}