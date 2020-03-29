package net.milosvasic.factory.mail.remote

import net.milosvasic.factory.mail.os.OperatingSystem

abstract class Connection<T : Remote>(private val remote: T) {

    var operatingSystem = OperatingSystem()

    abstract fun execute(data: String)
}