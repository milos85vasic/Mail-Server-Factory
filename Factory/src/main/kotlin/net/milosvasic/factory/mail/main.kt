@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.application.DefaultInitializationHandler
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.initialization.InitializationFlow
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
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}