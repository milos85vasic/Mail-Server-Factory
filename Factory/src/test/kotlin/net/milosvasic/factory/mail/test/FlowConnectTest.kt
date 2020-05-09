package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Test


class FlowConnectTest : BaseTest() {

    @Test
    fun testFlowConnect() {
        initLogging()
        log.i("Flow connect test started")

        var count = 0
        val echo = "Test"
        val iterations = 3
        var commandFlowResult: Boolean? = null
        var initializationFlowResult: Boolean? = null

        fun getEcho() = Commands.echo("$echo:${++count}")

        val commandFlowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                log.v("Command flow finished")
                if (!success) {
                    log.e(message)
                }
                assert(success)
                commandFlowResult = success
            }
        }

        val initializationFlowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                log.v("Initialization flow finished")
                if (!success) {
                    log.e(message)
                }
                assert(success)
                initializationFlowResult = success
            }
        }

        val initializers = mutableListOf<Initializer>()
        for (x in 0 until iterations) {
            val initializer = SimpleInitializer("Initializer no. ${x + 1}")
            initializers.add(initializer)
        }

        var initFlow = InitializationFlow()
        initializers.forEach {
            initFlow = initFlow.width(it)
        }
        initFlow.onFinish(initializationFlowCallback)

        var flow = CommandFlow()
        val terminal = Terminal()
        for (x in 0 until iterations) {
            flow = flow.width(terminal)
            for (y in 0 .. x) {
                flow = flow.perform(getEcho())
            }
        }
        flow
                .onFinish(commandFlowCallback)
                .connect(initFlow)
                .run()

        while (commandFlowResult == null || initializationFlowResult == null) {
            Thread.yield()
        }
        commandFlowResult?.let {
            assert(it)
        }
        initializationFlowResult?.let {
            assert(it)
        }

        log.i("Flow connect test completed")
    }
}