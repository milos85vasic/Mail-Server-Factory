package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.log

class DefaultFlowCallback : FlowCallback {

    override fun onFinish(success: Boolean, message: String) {

        if (success) {
            log.v("Flow finished")
        } else {
            log.w("Flow finished with errors: $message")
        }
    }
}