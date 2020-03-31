package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.component.installer.InstallationStep
import net.milosvasic.factory.mail.component.packaging.item.Envelope
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem
import net.milosvasic.factory.mail.component.packaging.item.Packages
import net.milosvasic.factory.mail.containing.ContainerSystem
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Install
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.operation.Uninstall
import net.milosvasic.factory.mail.os.OSType
import net.milosvasic.factory.mail.remote.ssh.SSH

class Docker(private val entryPoint: SSH) : ContainerSystem(entryPoint) {

    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            notify(result)
        }
    }

    override val steps: List<InstallationStep>
        get() = listOf(

            // TODO: To be implemented.
        )

    init {
        entryPoint.subscribe(listener)
    }

    override fun install() {

        // TODO: To be implemented.
        notify(OperationResult(Install(componentId), false))
    }

    override fun uninstall() {

        // TODO: To be implemented.
        notify(OperationResult(Uninstall(componentId), false))
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

    override fun terminate() {
        log.v("Shutting down: $this")
        entryPoint.unsubscribe(listener)
    }

    @Throws(IllegalStateException::class)
    override fun getDependencies(): List<List<InstallationItem>> {
        when (entryPoint.getRemoteOS().getType()) {
            OSType.CENTOS -> {
                return listOf(

                    // Initial dependencies:
                    listOf(
                        Group("Development Tools"),
                        Packages(
                            Envelope(
                                "yum-utils",
                                "device-mapper-persistent-data",
                                "lvm2"
                            )
                        )
                    ),

                    // Docker repository dependencies
                    listOf(
                        
                    )
                )
            }
            else -> {
                throw IllegalStateException("Can't obtain dependencies for unknown system")
            }
        }
    }
}