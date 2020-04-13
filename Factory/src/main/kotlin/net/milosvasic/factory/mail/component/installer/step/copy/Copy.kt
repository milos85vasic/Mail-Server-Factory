package net.milosvasic.factory.mail.component.installer.step.copy

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands

class Copy(what: String, where: String) : RemoteOperationInstallationStep<SSH>() {

    companion object {

        const val delimiter = ":"
    }

    private var command = "" // TODO:

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is Command -> {
                if (result.operation.toExecute.endsWith(command)) {

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
        val terminal = connection?.terminal
        if (terminal == null) {

            log.e("No terminal for copying")
            finish(false, CopyOperation())
        } else {

            command = "echo 'WORK IN PROGRESS'"
            terminal.execute(Command(command))
        }
    }
}