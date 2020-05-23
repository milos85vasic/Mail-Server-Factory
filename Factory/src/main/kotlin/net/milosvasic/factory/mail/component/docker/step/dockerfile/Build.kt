package net.milosvasic.factory.mail.component.docker.step.dockerfile

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.remote.ssh.SSH

class Build(what: String) : RemoteOperationInstallationStep<SSH>() {

    override fun getFlow(): CommandFlow {
        TODO("Not yet implemented")
    }

    override fun getOperation() = BuildOperation()
}