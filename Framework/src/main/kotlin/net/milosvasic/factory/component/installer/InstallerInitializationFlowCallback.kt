package net.milosvasic.factory.component.installer

import net.milosvasic.factory.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.log

class InstallerInitializationFlowCallback : DieOnFailureCallback() {

    override fun onFinish(success: Boolean) {
        super.onFinish(success)
        if (success) {
            log.i("Installer has been initialized")
        }
    }
}