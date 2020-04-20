package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

class Check(containerName: String) : ConditionCheck(containerName) {

    private val operation = CheckOperation()

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is SSHCommand -> {
                if (result.operation.command.endsWith(command)) {
                    finish(result.success, operation)
                }
            }
        }
    }
}