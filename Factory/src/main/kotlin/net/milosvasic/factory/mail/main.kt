@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.application.ServerFactory
import net.milosvasic.factory.mail.execution.flow.command.CommandFlow
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File

fun main(args: Array<String>) {

    initLogging()



    val terminal = Terminal()
    CommandFlow()
            .width(terminal)
            .perform(TerminalCommand(Commands.echo("Moja Maja")))
            .perform("Moja Maja 2")
            .perform(TerminalCommand(Commands.echo("Moja Maja 3")))
            .perform("Moja Maja 4")
            .run()

    return // TODO: Remove when flow implementation is complete.



    ServerFactory().run(args)
}

private fun initLogging() {
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}