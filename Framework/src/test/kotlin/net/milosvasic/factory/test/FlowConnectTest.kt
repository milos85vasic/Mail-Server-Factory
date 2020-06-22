package net.milosvasic.factory.test

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.DataHandler
import net.milosvasic.factory.common.initialization.Initializer
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationHandler
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.terminal.Terminal
import net.milosvasic.factory.terminal.command.EchoCommand
import net.milosvasic.factory.test.implementation.SimpleInitializer
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

        fun getEcho(parent: Int) = EchoCommand("$echo $parent :: ${++count}")

        val dataHandler = object : DataHandler<OperationResult> {
            override fun onData(data: OperationResult?) {
                log.v("Data: $data")
                Assertions.assertNotNull(data)
                Assertions.assertNotNull(data?.data)
                assert(data?.data != String.EMPTY)
                data?.let {
                    dataReceived.add(it.data)
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

        val commandFlowCallback = object : FlowCallback {
            override fun onFinish(success: Boolean) {
                if (success) {
                    log.i("Command flow finished")
                } else {
                    log.e("Command flow failed")
                }
                assert(success)
                commandFlowExecuted++
            }
        }

        val initializationFlowCallback = object : FlowCallback {
            override fun onFinish(success: Boolean) {
                if (success) {
                    log.i("Initialization flow finished")
                } else {
                    log.e("Initialization flow failed")
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