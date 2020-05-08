package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.compositeLogger
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class CommandFlowTest {

    @Test
    fun testCommandFloe() {
        initLogging()
        log.i("Test: STARTED")

        var count = 0
        val echo = "Test"
        var finished = false
        val dataReceived = mutableListOf<String>()

        fun getEcho() = Commands.echo("$echo:${++count}")

        val dataHandler = object : DataHandler<String> {
            override fun onData(data: String?) {
                log.v("Data: $data")
                Assertions.assertNotNull(data)
                assert(data != String.EMPTY)
                data?.let {
                    dataReceived.add(it)
                }
            }
        }

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
                assert(success)
                finished = true
            }
        }

        var sum = 0
        var flow = CommandFlow()
        val terminal = Terminal()
        val iterations = listOf(1, 2, 3, 4, 5, 6, 7)
        iterations.forEach {
            sum += it
            flow = flow.width(terminal)
            for (x in 0 until it) {
                flow = flow.perform(TerminalCommand(getEcho()), dataHandler)
            }
        }
        flow
            .onFinish(flowCallback)
            .run()

        while (!finished) {
            Thread.yield()
        }

        for (x in 1 until sum) {
            val expected = "$echo:$x"
            val compare = dataReceived[x - 1].replace("'", "")
            Assertions.assertEquals(expected, compare)
        }

        log.i("Test: COMPLETED")
    }

    private fun initLogging() {
        val console = ConsoleLogger()
        val filesystem = FilesystemLogger(File("."))
        compositeLogger.addLoggers(console, filesystem)
    }
}