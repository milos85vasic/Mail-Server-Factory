@file:JvmName("Launcher")

package net.milosvasic.factory

import net.milosvasic.factory.application.DefaultInitializationHandler
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File

fun main(args: Array<String>) {

    initLogging()
    val factory = MailServerFactory(args.toList())

    val callback = object : FlowCallback {
        override fun onFinish(success: Boolean) {

            if (success) {
                try {
                    log.i("Server factory initialized")
                    factory.run()
                } catch (e: IllegalStateException) {
                    fail(e)
                }
            }
        }
    }

    val handler = DefaultInitializationHandler()
    try {
        InitializationFlow()
                .width(factory)
                .handler(handler)
                .onFinish(callback)
                .run()

    } catch (e: BusyException) {

        fail(e)
    }
}

private fun initLogging() {
    tag = BuildInfo.NAME
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}