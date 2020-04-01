package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.Termination
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH

class Installer(
    private val configuration: SoftwareConfiguration,
    entryPoint: SSH
) :
    Component(),
    BusyDelegation,
    Installation,
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Initialization,
    Termination {

    private val busy = Busy()
    private val installer = PackageInstaller(entryPoint)
    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is PackageInstallerInitializationOperation -> {

                    // TODO: Create new result and send.
                    notify(result)
                }
            }
        }
    }

    override val steps: List<InstallationStep>
        get() = listOf(

            // TODO: Steps
        )

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        busy()
        installer.initialize()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()

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
    override fun isInitialized() = installer.isInitialized()



    @Synchronized
    override fun install() {

        // TODO
//        installations.forEach {
//            it.subscribe(listener)
//            it.install()
//        }
    }

    @Synchronized
    override fun uninstall() {

        // TODO
//        installations.forEach {
//            it.subscribe(listener)
//            it.uninstall()
//        }
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun busy() {
        BusyWorker.busy(busy)
    }

    @Synchronized
    override fun free() {
        BusyWorker.free(busy)
    }
}