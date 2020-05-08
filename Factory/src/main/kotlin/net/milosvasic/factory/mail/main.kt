@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.application.server_factory.ServerFactory
import net.milosvasic.factory.mail.application.server_factory.ServerFactoryInitializationOperation
import net.milosvasic.factory.mail.application.server_factory.ServerFactoryTerminationOperation
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    initLogging()
    val factory = ServerFactory(args.toList())

    val initializationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is ServerFactoryInitializationOperation -> {
                    if (result.success) {
                        try {
                            log.i("Server factory initialized")
                            factory.run()
                        } catch (e: IllegalStateException) {

                            log.e(e)
                            fail(ERROR.RUNTIME_ERROR)
                        }
                    } else {
                        fail(ERROR.INITIALIZATION_FAILURE)
                    }
                }
                is ServerFactoryTerminationOperation -> {
                    if (result.success) {
                        log.i("Server factory terminated")
                        exitProcess(0)
                    } else {
                        fail(ERROR.TERMINATION_FAILURE)
                    }
                }
                else -> {
                    fail(ERROR.UNEXPECTED_EVENT_RECEIVED)
                }
            }
        }
    }

    factory.subscribe(initializationCallback)
    try {
        factory.initialize()
    } catch (e: IllegalStateException) {

        log.e(e)
        fail(ERROR.INITIALIZATION_FAILURE)
    }
}

private fun initLogging() {
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}