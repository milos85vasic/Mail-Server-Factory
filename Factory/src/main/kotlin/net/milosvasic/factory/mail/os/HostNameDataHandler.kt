package net.milosvasic.factory.mail.os

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult

class HostNameDataHandler(
        private val os: OperatingSystem,
        private val hostname: String = String.EMPTY

) : DataHandler<OperationResult> {

    override fun onData(data: OperationResult?) {

        data?.let {
            if (!data.success) {

                log.e("Hostname operation failed")
                return
            }

            fun setHostname(hostname: String) {

                try {
                    if (os.getHostname() != hostname) {
                        os.setHostname(hostname)
                        log.i("Hostname: ${os.getHostname()}")
                    }
                } catch (e: IllegalArgumentException) {
                    log.e(e)
                }
            }

            if (hostname != String.EMPTY) {
                setHostname(hostname)
            } else {
                setHostname(it.data)
            }
        }
        if (data == null) {
            log.e("No hostname data received")
        }
    }
}