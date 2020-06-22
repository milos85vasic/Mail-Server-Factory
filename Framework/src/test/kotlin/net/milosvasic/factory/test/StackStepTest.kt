package net.milosvasic.factory.test

import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.fail
import net.milosvasic.factory.log
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.remote.ConnectionProvider
import net.milosvasic.factory.test.implementation.StubInstallationStepFactory
import net.milosvasic.factory.test.implementation.StubSSH
import net.milosvasic.factory.test.implementation.StubServerFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StackStepTest : BaseTest() {

    @Test
    fun testStackStep() {
        initLogging()
        log.i("Stack step flow test started")

        val ssh = StubSSH()
        var initialized = false
        val mocks = "Mocks/Stack/Main.json"

        val connectionProvider = object : ConnectionProvider {
            override fun obtain(): Connection {
                return ssh
            }
        }

        val stepFactory = StubInstallationStepFactory(listOf())
        InstallationStepFactories.addFactory(stepFactory)
        val factory = StubServerFactory(listOf(mocks))
        factory.setConnectionProvider(connectionProvider)

        val callback = object : FlowCallback {
            override fun onFinish(success: Boolean) {

                assert(success)
                initialized = success
                if (success) {
                    try {
                        log.i("Factory initialized")
                        factory.run()
                    } catch (e: IllegalStateException) {

                        fail(e)
                        Assertions.fail<String>()
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
            Assertions.fail<String>()
        }

        log.i("Stack step flow test completed")
    }
}