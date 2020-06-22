package net.milosvasic.factory

import net.milosvasic.factory.common.Logger
import net.milosvasic.factory.error.ERROR
import net.milosvasic.logger.CompositeLogger
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import kotlin.system.exitProcess

var tag = BuildInfo.NAME
const val localhost = "127.0.0.1"
val compositeLogger = CompositeLogger()

val log = object : Logger {

    override fun v(message: String) = compositeLogger.v(tag, message)

    override fun d(message: String) = compositeLogger.d(tag, message)

    override fun c(message: String) = compositeLogger.c(tag, message)

    override fun n(message: String) = compositeLogger.n(tag, message)

    override fun i(message: String) = compositeLogger.i(tag, message)

    override fun w(message: String) = compositeLogger.w(tag, message)

    override fun w(exception: Exception) {

        val message = getMessage(exception)
        compositeLogger.w(tag, message, ConsoleLogger::class)
        compositeLogger.w(tag, exception, FilesystemLogger::class)
    }

    override fun e(message: String) = compositeLogger.e(tag, message)

    override fun e(exception: Exception) {

        val message = getMessage(exception)
        compositeLogger.e(tag, message, ConsoleLogger::class)
        compositeLogger.e(tag, exception, FilesystemLogger::class)
    }

    private fun getMessage(exception: Exception): String {
        var message = "Error: $exception"
        exception.message?.let {
            message = it
        }
        return message
    }
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

    log.e(e)
    val error = ERROR.FATAL_EXCEPTION
    System.err.println(error.message)
    exitProcess(error.code)
}

val String.Companion.EMPTY: String
    get() = ""

val String.Companion.LINE_BREAK: String
    get() = "\n"



