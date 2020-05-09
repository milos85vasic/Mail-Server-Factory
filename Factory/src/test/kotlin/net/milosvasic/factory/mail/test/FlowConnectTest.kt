package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class FlowConnectTest : BaseTest() {

    @Test
    fun testFlowConnect() {
        initLogging()
        log.i("Flow connect test started")

        var count = 0
        val echo = "Test"
        val iterations = 3
        var commandFlowExecuted = 0
        var initializationFlowExecuted = 0

        fun getEcho() = Commands.echo("$echo:${++count}")

        val commandFlowCallback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    log.i("Command flow finished")
                } else {
                    log.e(message)
                }
                assert(success)
                commandFlowExecuted++
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

        fun getCommandFlow() : CommandFlow {
            var flow = CommandFlow()
            val terminal = Terminal()
            for (x in 0 until iterations) {
                flow = flow.width(terminal)
                for (y in 0 .. x) {
                    flow = flow.perform(getEcho())
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

        val flow = getCommandFlow()
        for (x in 0 until iterations) {
            flow
                    .connect(getInitFlow(++initFlows))
                    .connect(getCommandFlow())
        }
        flow.run()

        while (commandFlowExecuted < iterations + 1 || initializationFlowExecuted < iterations) {
            Thread.yield()
        }
        Assertions.assertEquals(iterations + 1, commandFlowExecuted)
        Assertions.assertEquals(iterations, initializationFlowExecuted)

        log.i("Flow connect test completed")
    }
}