package net.milosvasic.factory.mail.os

import net.milosvasic.factory.mail.log


data class OperatingSystem(
    private var name: String = "System unknown",
    private var type: OSType = OSType.UNKNOWN
) {

    fun parseAndSetSystemInfo(data: String) {
        val osLineString = "Operating System:"
        val lines = data.split("\n")
        lines.forEach {
            if (it.contains(osLineString)) {
                name = it.replace(osLineString, "").trim()
                if (name.toLowerCase().contains(OSType.CENTOS.osName.toLowerCase())) {
                    type = OSType.CENTOS
                    return
                }
                if (name.toLowerCase().contains(OSType.FEDORA.osName.toLowerCase())) {
                    type = OSType.FEDORA
                    return
                }
                if (name.toLowerCase().contains(OSType.REDHAT.osName.toLowerCase())) {
                    type = OSType.REDHAT
                    return
                }
                if (name.toLowerCase().contains(OSType.UBUNTU.osName.toLowerCase())) {
                    type = OSType.UBUNTU
                    return
                }
                if (name.toLowerCase().contains(OSType.DEBIAN.osName.toLowerCase())) {
                    type = OSType.DEBIAN
                    return
                }
                type = OSType.GENERIC
                return
            }
        }
    }

    fun getName() = name

    fun getType() = type
}