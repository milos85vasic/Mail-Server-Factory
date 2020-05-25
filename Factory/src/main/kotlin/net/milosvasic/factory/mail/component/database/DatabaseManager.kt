package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Registration
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.initialization.Termination
import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.validation.Validator

object DatabaseManager :
        ObtainParametrized<DatabaseRequest, Database>,
        Registration<DatabaseRegistration>,
        Termination {

    private val busy = Busy()
    private var registration: DatabaseRegistration? = null
    private val operation = DatabaseRegistrationOperation()
    private val databases = mutableMapOf<Type, MutableMap<String, Database>>()

    private val initFlowCallback = object : FlowCallback<String> {
        override fun onFinish(success: Boolean, message: String, data: String?) {

            registration?.let {

                val name = it.name
                val db = it.database
                val type = db.type

                if (success) {

                    var dbs = databases[type]
                    if (dbs == null) {
                        dbs = mutableMapOf()
                        databases[type] = dbs
                    }
                    dbs[name] = it.database
                } else {

                    if (message == String.EMPTY) {
                        log.e("Database initialization failed for ${type.type} database")
                    } else {
                        log.e(message)
                    }
                }
                val result = OperationResult(operation, success, data ?: String.EMPTY)
                it.callback.onOperationPerformed(result)
            }
            registration = null
            free()
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun register(what: DatabaseRegistration) {
        busy()

        registration = what
        val db = what.database
        InitializationFlow()
                .width(db)
                .onFinish(initFlowCallback)
                .run()
    }

    @Throws(IllegalArgumentException::class)
    override fun unRegister(what: DatabaseRegistration) {

        val name = what.name
        val db = what.database
        val type = db.type
        if (databases[type]?.get(name) == what.database) {
            databases[type]?.remove(name)?.terminate()

            val result = OperationResult(operation, true)
            what.callback.onOperationPerformed(result)
        } else {

            log.e("Database instance is not registered: $databases")
            val result = OperationResult(operation, false)
            what.callback.onOperationPerformed(result)
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun obtain(vararg param: DatabaseRequest): Database {

        Validator.Arguments.validateSingle(param)
        val request = param[0]
        val type = request.type
        val name = request.name
        databases[type]?.get(name)?.let {
            return it
        }
        throw IllegalArgumentException("No database registered for the type: ${type.type}")
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        busy()
        databases.keys.forEach { type ->
            databases[type]?.keys?.forEach {name ->
                unRegister(type, name)
            }
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

    private fun unRegister(type: Type, name: String) {
        databases[type]?.remove(name)?.terminate()
    }
}