package net.milosvasic.factory.component.docker

import net.milosvasic.factory.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.log

class DockerInitializationFlowCallback : DieOnFailureCallback() {

    override fun onFinish(success: Boolean) {
        super.onFinish(success)
        if (success) {
            log.i("Docker has been initialized")
        }
    }
}