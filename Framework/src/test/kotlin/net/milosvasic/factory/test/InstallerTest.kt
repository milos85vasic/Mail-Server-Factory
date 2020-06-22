package net.milosvasic.factory.test

import net.milosvasic.factory.component.installer.Installer
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.test.implementation.StubPackageManager
import net.milosvasic.factory.test.implementation.StubSSH
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

        val flowCallback = object : FlowCallback {
            override fun onFinish(success: Boolean) {

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