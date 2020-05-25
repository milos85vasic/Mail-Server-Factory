package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.common.initialization.Termination
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.remote.Connection

abstract class Database(entryPoint: Connection) :
        BusyWorker<InstallationStep<*>>(entryPoint), Initializer, Termination {

    abstract val type: Type

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

    @Throws(IllegalStateException::class)
    protected abstract fun initialization()

    @Throws(IllegalStateException::class)
    protected abstract fun termination()
}
