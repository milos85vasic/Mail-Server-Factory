package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
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
    private val entryPoint: SSH
) :
    Component(),
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

    override fun initialize() {

    }

    override fun terminate() {

    }

    override fun checkInitialized() {

    }

    override fun checkNotInitialized() {

    }

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
}