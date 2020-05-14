package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationHandler
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
        var initialized = 0
        val dataReceived = mutableListOf<String>()

        fun getEcho(parent: Int) = Commands.echo("$echo $parent :: ${++count}")

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

        val initHandler = object : InitializationHandler {
            override fun onInitialization(initializer: Initializer, success: Boolean) {
                assert(success)
                assert(initializer is SimpleInitializer)
                initialized++
            }

            override fun onTermination(initializer: Initializer, success: Boolean) {
                // Ignore.
            }
        }

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

        var commandFlows = 0
        var commandFlowsExpectedCount = 0
        fun getCommandFlow(parent: Int) : CommandFlow {
            var flow = CommandFlow()
            val terminal = Terminal()
            for (x in 0 until iterations) {
                flow = flow.width(terminal)
                for (y in 0 .. x) {
                    commandFlowsExpectedCount++
                    flow = flow.perform(getEcho(parent), dataHandler)
                }
            }
            flow.onFinish(commandFlowCallback)
            return flow
        }

        var initFlows = 0
        var initFlowsExpectedCount = 0
        fun getInitFlow(parent: Int) : InitializationFlow {
            val initializers = mutableListOf<Initializer>()
            for (x in 0 until iterations) {
                val initializer = SimpleInitializer("Initializer $parent :: ${x + 1}")
                initializers.add(initializer)
            }
            var initFlow = InitializationFlow()
            initializers.forEach {
                initFlowsExpectedCount++
                initFlow = initFlow.width(it, initHandler)
            }
            initFlow.onFinish(initializationFlowCallback)
            return initFlow
        }

        val flow = getCommandFlow(++commandFlows)
        for (x in 0 until iterations) {
            flow
                    .connect(getInitFlow(++initFlows))
                    .connect(getCommandFlow(++commandFlows))
        }
        flow.run()

        while (flow.isBusy()) {
            Thread.yield()
        }
        Assertions.assertEquals(iterations + 1, commandFlowExecuted)
        Assertions.assertEquals(iterations, initializationFlowExecuted)
        Assertions.assertEquals(commandFlowsExpectedCount, dataReceived.size)
        Assertions.assertEquals(initFlowsExpectedCount, initialized)

        log.i("Flow connect test completed")
    }
}