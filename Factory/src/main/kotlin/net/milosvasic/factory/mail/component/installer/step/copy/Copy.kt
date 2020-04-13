package net.milosvasic.factory.mail.component.installer.step.copy

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

class Copy(what: String, where: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {

        val delimiter = ":"
    }

    private var command = "" // TODO:

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is SSHCommand -> {
                if (result.operation.command.endsWith(command)) {

                    // TODO:
                    finish(result.success, CopyOperation())
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: SSH) {
        super.execute(*params)
        connection?.execute(command)
    }
}