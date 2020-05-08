package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.log
import org.junit.jupiter.api.Test

class InitializationFlowTest : BaseTest() {

    @Test
    fun testInitializationFlow() {
        initLogging()
        log.i("Test: STARTED")



        log.i("Test: COMPLETED")
    }
}