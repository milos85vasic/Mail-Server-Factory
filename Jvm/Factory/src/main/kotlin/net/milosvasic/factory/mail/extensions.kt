package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.common.Logger
import net.milosvasic.factory.mail.error.ERROR
import java.lang.StringBuilder
import kotlin.system.exitProcess

import net.milosvasic.logger.CompositeLogger

const val localhost = "127.0.0.1"
val compositeLogger = CompositeLogger()

val log = object : Logger {

    private val tag = BuildInfo.NAME

    override fun v(message: String) = compositeLogger.v(tag, message)

    override fun d(message: String) = compositeLogger.d(tag, message)

    override fun c(message: String) = compositeLogger.c(tag, message)

    override fun n(message: String) = compositeLogger.n(tag, message)

    override fun i(message: String) = compositeLogger.i(tag, message)

    override fun w(message: String) = compositeLogger.w(tag, message)

    override fun e(message: String) = compositeLogger.e(tag, message)
}

fun fail(error: ERROR) {

    System.err.println(error.message)
    exitProcess(error.code)
}

fun fail(error: ERROR, with: String) {

    log.e("${error.message}: $with")
    exitProcess(error.code)
}

fun fail(error: ERROR, vararg with: Any) {

    val builder = StringBuilder()
    with.forEach {
        builder.append(it.toString()).append("\n")
    }
    fail(error, builder)
}

fun fail(e: Exception) {

    e.printStackTrace()
    e.message?.let {
        fail(ERROR.FATAL_EXCEPTION, it)
    }
}

val String.Companion.EMPTY: String
    get() = ""
