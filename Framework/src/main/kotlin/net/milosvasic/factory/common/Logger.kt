package net.milosvasic.factory.common

interface Logger {

    fun v(message: String)

    fun d(message: String)

    fun c(message: String)

    fun n(message: String)

    fun i(message: String)

    fun w(message: String)

    fun w(exception: Exception)

    fun e(message: String)

    fun e(exception: Exception)
}