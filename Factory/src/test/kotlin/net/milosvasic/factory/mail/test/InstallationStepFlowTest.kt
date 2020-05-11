package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Test

class InstallationStepFlowTest : BaseTest() {

    @Test
    fun testInstallationStepFlow() {
        initLogging()
        log.i("Command flow test started")

        val connection = StubConnection()
        val flow = InstallationStepFlow(connection)

        log.i("Command flow test completed")
    }
}