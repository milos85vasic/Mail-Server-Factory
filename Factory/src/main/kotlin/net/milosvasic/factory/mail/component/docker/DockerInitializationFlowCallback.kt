package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.mail.log

class DockerInitializationFlowCallback : DieOnFailureCallback<String>() {

    override fun onFinish(success: Boolean, message: String, data: String?) {
        super.onFinish(success, message, data)
        if (success) {
            log.i("Docker has been initialized")
        }
    }
}