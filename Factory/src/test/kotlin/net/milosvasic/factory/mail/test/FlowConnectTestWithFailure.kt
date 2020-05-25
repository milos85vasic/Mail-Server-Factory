package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.command.EchoCommand
import net.milosvasic.factory.mail.terminal.command.RawTerminalCommand
import net.milosvasic.factory.mail.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FlowConnectTestWithFailure : BaseTest() {

    @Test
    fun testFlowConnect() {
        initLogging()
        log.i("Flow connect test with failure started")

        var count = 0
        val echo = "Test"
        val iterations = 3
        var commandFlowFailed = 0
        var commandFlowExecuted = 0
        var initializationFlowExecuted = 0

        fun getEcho(parent: Int) = EchoCommand("$echo $parent :: ${++count}")

        val commandFlowCallback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    log.i("Command flow finished")
                    commandFlowExecuted++
                } else {
                    log.i(message)
                    commandFlowFailed++
                }
            }
        }

        val initializationFlowCallback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    log.i("Initialization flow finished")
                } else {
                    log.e(message)
                }
                assert(success)
                initializationFlowExecuted++
            }
        }

        var commandFlows = 0
        fun getCommandFlow(parent: Int, doFail: Boolean) : CommandFlow {
            var flow = CommandFlow()
            val terminal = Terminal()
            if (doFail) {
                flow = flow.width(terminal)
                flow = flow.perform(RawTerminalCommand("This command does not exist"))
            } else {
                for (x in 0 until iterations) {
                    flow = flow.width(terminal)
                    for (y in 0..x) {
                        flow = flow.perform(getEcho(parent))
                    }
                }
            }
            flow.onFinish(commandFlowCallback)
            return flow
        }

        var initFlows = 0
        fun getInitFlow(parent: Int) : InitializationFlow {
            val initializers = mutableListOf<Initializer>()
            for (x in 0 until iterations) {
                val initializer = SimpleInitializer("Initializer $parent :: ${x + 1}")
                initializers.add(initializer)
            }
            var initFlow = InitializationFlow()
            initializers.forEach {
                initFlow = initFlow.width(it)
            }
            initFlow.onFinish(initializationFlowCallback)
            return initFlow
        }

        val flow = getCommandFlow(++commandFlows, false)
        for (x in 0 until iterations) {
            flow
                    .connect(getInitFlow(++initFlows))
                    .connect(getCommandFlow(++commandFlows, true))
        }
        flow.run()

        while (flow.isBusy()) {
            Thread.yield()
        }

        Assertions.assertEquals(1, commandFlowExecuted)
        Assertions.assertEquals(1, commandFlowFailed)
        Assertions.assertEquals(1, initializationFlowExecuted)

        log.i("Flow connect test with failure completed")
    }
}