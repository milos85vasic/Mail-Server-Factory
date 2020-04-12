package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.operation.OperationResult


class Stack(private val composeYmlPath: String) : DockerInstallationStep() {

    override fun handleResult(result: OperationResult) {

        // TODO:
    }


}