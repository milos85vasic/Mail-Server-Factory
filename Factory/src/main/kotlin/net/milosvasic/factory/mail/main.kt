@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.application.server_factory.ServerFactory
import net.milosvasic.factory.mail.application.server_factory.ServerFactoryTerminationOperation
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
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
                is ServerFactoryTerminationOperation -> {
                    if (result.success) {
                        log.i("Server factory terminated")
                        exitProcess(0)
                    } else {
                        fail(ERROR.TERMINATION_FAILURE)
                    }
                }
            }
        }
    }

    factory.subscribe(initializationCallback)

    val callback = object : FlowCallback<String> {
        override fun onFinish(success: Boolean, message: String, data: String?) {

            if (success) {
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
    }

    InitializationFlow()
            .width(factory) // TODO: Terminate width parameter - passed like we did with DataHandler
            .onFinish(callback)
            .run()
}

private fun initLogging() {
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}