package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubPackageManager
import net.milosvasic.factory.mail.test.implementation.StubSSH
import org.junit.jupiter.api.Test

class InstallerTest : BaseTest() {

    @Test
    fun testPackageInstaller() {
        initLogging()
        log.i("Installer test started")

        val ssh = StubSSH()
        var initialized = false
        val installer = Installer(ssh)
        val stubPackageManager = StubPackageManager(ssh)
        installer.addSupportedPackageManager(stubPackageManager)

        val flowCallback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {

                if (!success) {
                    log.w(message)
                }
                initialized = success
            }
        }

        val flow = InitializationFlow()
                .width(installer)
                .onFinish(flowCallback)

        flow.run()

        while (flow.isBusy()) {
            Thread.yield()
        }

        installer.terminate()
        assert(initialized)
        log.i("Installer test completed")
    }
}