package net.milosvasic.factory.mail.os


data class OperatingSystem(
    private var name: String = "System unknown",
    private var type: OSType = OSType.UNKNOWN
) {

    fun parseAndSetSystemInfo(data: String) {

        // TODO:

    }

    fun getName() = name

    fun getType() = type
}