package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.mail.log

class InstallerInitializationFlowCallback : DieOnFailureCallback() {

    override fun onFinish(success: Boolean) {
        super.onFinish(success)
        if (success) {
            log.i("Installer has been initialized")
        }
    }
}