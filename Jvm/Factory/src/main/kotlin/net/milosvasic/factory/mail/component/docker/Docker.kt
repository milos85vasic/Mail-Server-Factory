package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.containing.ContainerSystem
import net.milosvasic.factory.mail.operation.Install
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.operation.Uninstall
import net.milosvasic.factory.mail.remote.ssh.SSH

class Docker(private val entryPoint: SSH) : ContainerSystem(entryPoint) {

    private val subscribers = mutableSetOf<OperationResultListener>()

    override fun install() {

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
}