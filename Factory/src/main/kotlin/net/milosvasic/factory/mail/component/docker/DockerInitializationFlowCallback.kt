package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.mail.log

class DockerInitializationFlowCallback : DieOnFailureCallback() {

    override fun onFinish(success: Boolean, message: String) {
        super.onFinish(success, message)
        if (success) {
            log.i("Docker has been initialized")
        }
    }
}