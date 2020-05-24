package net.milosvasic.factory.mail.os

import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult

class HostNameDataHandler(private val os: OperatingSystem) : DataHandler<OperationResult> {

    override fun onData(data: OperationResult?) {

        data?.let {
            try {
                os.setHostname(it.data)
                log.i("Hostname: ${os.getHostname()}")
            } catch (e: IllegalArgumentException) {

                log.e(e)
            }
        }
        if (data == null) {
            log.e("No hostname data received")
        }
    }
}