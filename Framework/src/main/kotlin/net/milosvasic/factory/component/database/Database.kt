package net.milosvasic.factory.component.database

import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.common.initialization.Initializer
import net.milosvasic.factory.common.initialization.Termination
import net.milosvasic.factory.component.database.command.DatabaseSqlCommand
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import java.util.concurrent.atomic.AtomicBoolean

abstract class Database(val name: String, val connection: DatabaseConnection) :

        BusyWorker<InstallationStep<*>>(connection.entryPoint),
        Initializer,
        Termination {

    abstract val type: Type
    protected val initialized = AtomicBoolean()

    @Synchronized
    @Throws(IllegalStateException::class)
    final override fun initialize() {
        checkInitialized()
        busy()
        initialization()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    final override fun terminate() {
        checkNotInitialized()
        log.v("Shutting down: $this")
        termination()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (isInitialized()) {
            throw IllegalStateException("Installer has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("Installer has not been initialized")
        }
    }

    override fun isInitialized() = initialized.get()

    @Synchronized
    override fun notify(success: Boolean) {
        val operation = getNotifyOperation()
        val result = OperationResult(operation, success)
        notify(result)
    }

    override fun onSuccessResult() {
        free(true)
    }

    override fun onFailedResult() {
        free(false)
    }

    @Throws(IllegalStateException::class)
    protected abstract fun initialization()

    @Throws(IllegalStateException::class)
    protected abstract fun termination()

    abstract fun getInstallation(): InstallationStepFlow

    abstract fun getSqlCommand(sql: String): DatabaseSqlCommand

    private fun getNotifyOperation() = DatabaseInitializationOperation()
}
