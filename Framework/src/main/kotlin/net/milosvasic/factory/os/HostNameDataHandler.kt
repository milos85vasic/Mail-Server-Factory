package net.milosvasic.factory.os

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.DataHandler
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult

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