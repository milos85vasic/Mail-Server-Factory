package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.Initialization
import java.io.File

object ConfigurationManager : Initialization {

    private val busy = Busy()
    private var configurationPath = String.EMPTY
    private var configuration: Configuration? = null

    @Throws(IllegalArgumentException::class, BusyException::class, IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        BusyWorker.busy(busy)
        val file = File(configurationPath)
        configuration = Configuration.obtain(file)
        BusyWorker.free(busy)
    }

    @Throws(IllegalStateException::class)
    fun getConfiguration(): Configuration {
        checkNotInitialized()
        return configuration!!
    }

    @Synchronized
    override fun isInitialized(): Boolean {
        return configuration != null
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun setConfigurationPath(path: String) {
        checkInitialized()
        val validator = ConfigurationPathValidator()
        if (validator.validate(path)) {
            configurationPath = path
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (isInitialized()) {
            throw IllegalStateException("Configuration manager has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("Configuration manager has not been initialized")
        }
    }
}