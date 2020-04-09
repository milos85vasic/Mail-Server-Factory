package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.configuration.ConfigurableSoftware
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.remote.Connection

abstract class InstallerAbstract(entryPoint: Connection) :
        BusyWorker<InstallationStep<*>>(entryPoint),
        ConfigurableSoftware,
        Installation,
        Initialization {

    protected var item: InstallationStep<*>? = null
    protected var config: SoftwareConfiguration? = null

    @Synchronized
    override fun install() {

        if (config == null) {

            log.e("No configuration available. Please set configuration before installation.")
            free(false)
            return
        } else {

            config?.let {
                try {
                    val steps = it.obtain(getEnvironmentName())
                    busy()
                    iterator = steps.iterator()
                    tryNext()
                } catch (e: IllegalArgumentException) {

                    log.e(e)
                    free(false)
                } catch (e: IllegalStateException) {

                    log.e(e)
                    free(false)
                }
            }
        }
    }

    @Synchronized
    @Throws(UnsupportedOperationException::class)
    override fun uninstall() {
        throw UnsupportedOperationException("Not implemented yet.")
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

    @Synchronized
    @Throws(BusyException::class)
    override fun setConfiguration(configuration: SoftwareConfiguration) {
        busy()
        this.config = configuration
        free()
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun clearConfiguration() {
        busy()
        config = null
        free()
    }

    abstract fun getEnvironmentName(): String
}