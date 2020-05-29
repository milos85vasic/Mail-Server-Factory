package net.milosvasic.factory.mail.component.installer.step.certificate

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.remote.ssh.SSH

class Certificate(val name: String) : RemoteOperationInstallationStep<SSH>() {

    override fun getFlow(): CommandFlow {
        TODO("Not yet implemented")
    }

    override fun getOperation(): Operation {
        TODO("Not yet implemented")
    }
}