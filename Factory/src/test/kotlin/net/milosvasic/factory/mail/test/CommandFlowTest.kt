package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.command.EchoCommand
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommandFlowTest : BaseTest() {

    @Test
    fun testCommandFlow() {
        initLogging()
        log.i("Command flow test started")

        var count = 0
        val echo = "Test"
        var finished = false
        val dataReceived = mutableListOf<String>()

        fun getEcho() = EchoCommand("$echo:${++count}")

        val dataHandler = object : DataHandler<OperationResult> {
            override fun onData(data: OperationResult?) {
                log.v("Data: ${data?.data}")
                Assertions.assertNotNull(data)
                Assertions.assertNotNull(data?.data)
                assert(data?.data != String.EMPTY)
                data?.let {
                    dataReceived.add(it.data)
                }
            }
        }

        val flowCallback = object : FlowCallback {

            override fun onFinish(success: Boolean, message: String) {
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
                flow = flow.perform(getEcho(), dataHandler)
            }
        }
        flow
            .onFinish(flowCallback)
            .run()

        while (flow.isBusy()) {
            Thread.yield()
        }

        assert(finished)
        for (x in 1 until sum) {
            val expected = "$echo:$x"
            val compare = dataReceived[x - 1].replace("'", "")
            Assertions.assertEquals(expected, compare)
        }

        log.i("Command flow test completed")
    }
}