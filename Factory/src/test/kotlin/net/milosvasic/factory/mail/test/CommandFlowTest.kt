package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.compositeLogger
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import org.junit.jupiter.api.Test
import java.io.File

class CommandFlowTest {

    @Test
    fun testCommandFloe() {
        initLogging()

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                log.e(message)
                assert(success)
            }
        }

        val echo = "Test"
        val terminal = Terminal()
        CommandFlow()
                .width(terminal)
                .perform(Commands.echo(echo))
                .perform(TerminalCommand(Commands.echo(echo)))
                .perform(Commands.echo(echo))
                .width(terminal)
                .perform(TerminalCommand(Commands.echo(echo)))
                .perform(Commands.echo(echo))
                .perform(TerminalCommand(Commands.echo(echo)))
                .perform(Commands.echo(echo))
                .onFinish(flowCallback)
                .run()
    }

    private fun initLogging() {
        val console = ConsoleLogger()
        val filesystem = FilesystemLogger(File("."))
        compositeLogger.addLoggers(console, filesystem)
    }
}