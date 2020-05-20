package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.mail.log

class InstallerInitializationFlowCallback : DieOnFailureCallback<String>() {

    override fun onFinish(success: Boolean, message: String, data: String?) {
        super.onFinish(success, message, data)
        if (success) {
            log.i("Installer has been initialized")
        }
    }
}