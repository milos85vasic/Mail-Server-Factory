package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Test

class InitializationFlowTest : BaseTest() {

    @Test
    fun testInitializationFlow() {
        initLogging()
        log.i("Test: STARTED")
        val initializers = mutableListOf<Initializer>()
        for (x in 0..5) {
            val initializer = SimpleInitializer()
            initializers.add(initializer)
        }

        var finished = false
        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
                assert(success)
                finished = true
            }
        }

        var flow = InitializationFlow()
        initializers.forEach {
            flow = flow.width(it)
        }
        flow
                .onFinish(flowCallback)
                .run()

        while (!finished) {
            Thread.yield()
        }
        log.i("Test: COMPLETED")
    }
}