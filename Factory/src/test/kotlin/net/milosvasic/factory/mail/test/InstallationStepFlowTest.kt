package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Test

class InstallationStepFlowTest : BaseTest() {

    @Test
    fun testInstallationStepFlow() {
        initLogging()
        log.i("Command flow test started")

        val iterations = 5
        var finished = false
        val connection = StubConnection()
        val factory = InstallationStepFactory()
        var flow = InstallationStepFlow(connection)

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
                assert(success)
                finished = true
            }
        }

        for (x in 0 until iterations) {
            val definition = InstallationStepDefinition(
                    type = InstallationStepType.COMMAND.type,
                    value = "echo 'Test: $x'"
            )
            val installationStep = factory.obtain(definition)
            flow = flow.width(installationStep)
        }
        flow.onFinish(flowCallback).run()

        while (!finished) {
            Thread.yield()
        }

        log.i("Command flow test completed")
    }
}