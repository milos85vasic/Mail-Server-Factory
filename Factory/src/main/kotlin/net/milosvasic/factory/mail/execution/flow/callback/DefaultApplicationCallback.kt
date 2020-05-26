package net.milosvasic.factory.mail.execution.flow.callback

import net.milosvasic.factory.mail.log

class DefaultApplicationCallback : FlowCallback {

    override fun onFinish(success: Boolean, message: String) {

        if (!success) {
            log.w(message)
        }
    }
}