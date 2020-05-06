@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.application.ServerFactory
import net.milosvasic.factory.mail.execution.flow.FlowCallback
import net.milosvasic.factory.mail.execution.flow.command.CommandFlow
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    initLogging()


    val flowCallback = object : FlowCallback{
        override fun onFinish(success: Boolean, message: String) {
            if (success) {

                log.i("Flow finished")
            } else {

                log.e(message)
            }
            //exitProcess(0)
        }
    }

    val terminal = Terminal()
    CommandFlow()
            .width(terminal)
            .perform(TerminalCommand(Commands.echo("Moja Maja")))
            .perform(Commands.echo("Moja Maja 2"))
            .perform(TerminalCommand(Commands.echo("Moja Maja 3")))
            .perform(Commands.echo("Moja Maja 4"))
            .onFinish(flowCallback)
            .run()

    return // TODO: Remove when flow implementation is complete.



    ServerFactory().run(args)
}

private fun initLogging() {
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}