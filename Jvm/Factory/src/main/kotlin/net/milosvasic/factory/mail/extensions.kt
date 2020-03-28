package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.common.Execution
import net.milosvasic.factory.mail.common.Logger
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.execution.TaskExecutor
import java.lang.StringBuilder
import kotlin.system.exitProcess

import net.milosvasic.logger.CompositeLogger
import java.util.concurrent.atomic.AtomicBoolean

val busy = AtomicBoolean()
val compositeLogger = CompositeLogger()
val centralExecutor = TaskExecutor.instantiate(1)

val executor = object : Execution<Runnable> {

    @Synchronized
    override fun execute(what: Runnable) {
        centralExecutor.execute {
            busy.set(true)
            what.run()
            busy.set(false)
        }
    }
}

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