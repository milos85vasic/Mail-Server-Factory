package net.milosvasic.factory.test

import net.milosvasic.factory.common.initialization.Initializer
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationHandler
import net.milosvasic.factory.log
import net.milosvasic.factory.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InitializationFlowTestWithHandler : BaseTest() {

    @Test
    fun testInitializationFlowWithHandler() {
        initLogging()
        log.i("Initialization flow with handler test started")

        val count = 5
        val initializers = mutableListOf<SimpleInitializer>()
        for (x in 0 until count) {
            val initializer = SimpleInitializer("Initializer no. ${x + 1}")
            initializers.add(initializer)
        }

        var finished = false
        val flowCallback = object : FlowCallback {

            override fun onFinish(success: Boolean) {
                assert(success)
                finished = true
            }
        }

        var initialized = 0
        var terminated = 0
        val handler = object : InitializationHandler {
            override fun onInitialization(initializer: Initializer, success: Boolean) {
                assert(success)
                assert(initializer is SimpleInitializer)
                (initializer as SimpleInitializer).run()
                initialized++
            }

            override fun onTermination(initializer: Initializer, success: Boolean) {
                assert(success)
                terminated++
            }
        }

        var flow = InitializationFlow()
        initializers.forEach {
            flow = flow.width(it, handler)
        }
        flow
                .onFinish(flowCallback)
                .run()

        while (initialized < count || terminated < count) {
            Thread.yield()
        }

        Assertions.assertEquals(count, initialized)
        Assertions.assertEquals(count, terminated)
        assert(finished)
        log.i("Initialization flow with handler test completed")
    }
}