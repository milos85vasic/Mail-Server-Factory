package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.log
import org.junit.jupiter.api.Test

class StackStepTest : BaseTest() {

    @Test
    fun testStackStep() {
        initLogging()
        log.i("Deploy step flow test started")



        log.i("Deploy step flow test completed")
    }
}