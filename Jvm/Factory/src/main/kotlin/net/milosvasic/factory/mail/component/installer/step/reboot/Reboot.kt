package net.milosvasic.factory.mail.component.installer.step.reboot

import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.remote.Connection

class Reboot(private val timeoutInSeconds: Int = 120) : InstallationStep<Connection>() {

    @Synchronized
    @Throws(IllegalArgumentException::class)
    override fun execute(vararg params: Connection) {

        log.v("Reboot timeout in seconds: $timeoutInSeconds")

        // TODO: Execute and send RebootOperation
    }
}