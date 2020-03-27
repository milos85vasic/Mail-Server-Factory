package net.milosvasic.factory.mail

interface Logger {

    fun v(message: String)

    fun d(message: String)

    fun c(message: String)

    fun n(message: String)

    fun i(message: String)

    fun w(message: String)

    fun e(message: String)
}