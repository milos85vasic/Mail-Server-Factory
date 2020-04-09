package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private var configuration: SoftwareConfiguration? = null

    @Synchronized
    @Throws(BusyException::class)
    override fun setConfiguration(configuration: SoftwareConfiguration) {
        busy()
        this.configuration = configuration
        free()
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun clearConfiguration() {
        busy()
        configuration = null
        free()
    }

    override fun install() {
        TODO("Not yet implemented")
    }

    override fun uninstall() {
        TODO("Not yet implemented")
    }

    override fun tryNext() {

        // TODO: To be implemented.
    }

    override fun onSuccessResult() {

        // TODO: To be implemented.
    }

    override fun onFailedResult() {

        // TODO: To be implemented.
    }

    override fun handleResult(result: OperationResult) {

        // TODO: To be implemented.
    }

    override fun notify(success: Boolean) {

        // TODO: To be implemented.
    }

}