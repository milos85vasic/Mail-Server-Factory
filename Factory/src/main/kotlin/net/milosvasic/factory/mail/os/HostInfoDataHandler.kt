package net.milosvasic.factory.mail.os

import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.log

class HostInfoDataHandler(private val os: OperatingSystem) : DataHandler<String> {

    override fun onData(data: String?) {

        // log.w("> > > > > $data")

        if (data != null) {
            os.parseAndSetSystemInfo(data)
            if (os.getType() == OSType.UNKNOWN) {
                log.w("Host operating system is unknown")
            } else {
                log.i("Host operating system: ${os.getName()}")
            }
            if (os.getArchitecture() == Architecture.UNKNOWN) {
                log.w("Host system architecture is unknown")
            } else {
                val arch = os.getArchitecture().arch.toUpperCase()
                log.i("Host system architecture: $arch")
            }
        }
    }
}