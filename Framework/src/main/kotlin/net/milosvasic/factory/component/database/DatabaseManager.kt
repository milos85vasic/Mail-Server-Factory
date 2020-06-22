package net.milosvasic.factory.component.database

import net.milosvasic.factory.common.Registration
import net.milosvasic.factory.common.busy.Busy
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.common.initialization.Termination
import net.milosvasic.factory.common.obtain.ObtainParametrized
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.validation.Validator
import java.util.concurrent.ConcurrentHashMap

object DatabaseManager :
        ObtainParametrized<DatabaseRequest, Database>,
        Registration<DatabaseRegistration>,
        Termination {

    private val busy = Busy()
    private var registration: DatabaseRegistration? = null
    private val operation = DatabaseRegistrationOperation()
    private val databases = ConcurrentHashMap<Type, MutableMap<String, Database>>()

    private val initFlowCallback = object : FlowCallback {
        override fun onFinish(success: Boolean) {

            registration?.let {

                val db = it.database
                val name = db.name
                val type = db.type

                if (success) {

                    var dbs = databases[type]
                    if (dbs == null) {
                        dbs = mutableMapOf()
                        databases[type] = dbs
                    }
                    dbs[name] = it.database
                    log.i("$type database initialized: '$name'")
                } else {

                    log.e("Database initialization failed for ${type.type} database")
                }
                val result = OperationResult(operation, success)
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

        val db = what.database
        val name = db.name
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
        log.v("Shutting down: $this")
        val pairs = mutableListOf<Pair<Type, String>>()
        val iterator = databases.keys.iterator()
        while (iterator.hasNext()) {
            val type = iterator.next()
            val keyIterator = databases[type]?.keys?.iterator()
            keyIterator?.let {
                while (it.hasNext()) {
                    val name = it.next()
                    val pair = Pair(type, name)
                    pairs.add(pair)
                }
            }
        }
        pairs.forEach { pair ->
            unRegister(pair.first, pair.second)
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