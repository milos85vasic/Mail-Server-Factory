package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationHandler
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.SimpleInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InitializationFlowTest : BaseTest() {

//    @Test
//    fun testInitializationFlow() {
//        initLogging()
//        log.i("Test: STARTED")
//
//        val initializers = mutableListOf<Initializer>()
//        for (x in 0 until 5) {
//            val initializer = SimpleInitializer("Initializer no. ${x + 1}")
//            initializers.add(initializer)
//        }
//
//        var finished = false
//        val flowCallback = object : FlowCallback<String> {
//
//            override fun onFinish(success: Boolean, message: String, data: String?) {
//                if (!success) {
//                    log.e(message)
//                }
//                assert(success)
//                finished = true
//            }
//        }
//
//        var flow = InitializationFlow()
//        initializers.forEach {
//            flow = flow.width(it)
//        }
//        flow
//                .onFinish(flowCallback)
//                .run()
//
//        while (!finished) {
//            Thread.yield()
//        }
//        log.i("Test: COMPLETED")
//    }

    @Test
    fun testInitializationFlowWithHandler() {
        initLogging()
        log.i("Test: STARTED")

        val count = 5
        val initializers = mutableListOf<SimpleInitializer>()
        for (x in 0 until count) {
            val initializer = SimpleInitializer("Initializer no. ${x + 1}")
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

        while (!finished && initialized <= count && terminated <= count) {
            Thread.yield()
        }
        Thread.sleep(2000)

        Assertions.assertEquals(count, initialized)
        Assertions.assertEquals(count, terminated)
        log.i("Test: COMPLETED")
    }
}