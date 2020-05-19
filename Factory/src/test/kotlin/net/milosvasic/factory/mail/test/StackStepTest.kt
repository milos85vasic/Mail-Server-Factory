package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.application.server_factory.ServerFactory
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StackStepTest : BaseTest() {

    @Test
    fun testStackStep() {
        initLogging()
        log.i("Deploy step flow test started")

        var initialized = false
        val mocks = "Mocks/Stack/Main.json"
        val factory = ServerFactory(listOf(mocks))

        val callback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {

                assert(success)
                initialized = success
                if (success) {
                    try {
                        log.i("Factory initialized")
                        factory.run()
                    } catch (e: IllegalStateException) {

                        fail(e)
                        Assertions.fail()
                    }
                }
            }
        }

        try {
            val flow = InitializationFlow()
                    .width(factory)
                    .onFinish(callback)

            flow.run()

            while (flow.isBusy() || factory.isBusy()) {
                Thread.yield()
            }

            assert(initialized)

        } catch (e: BusyException) {

            fail(e)
            Assertions.fail()
        }

        log.i("Deploy step flow test completed")
    }
}