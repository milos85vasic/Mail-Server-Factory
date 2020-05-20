package net.milosvasic.factory.mail.execution.flow.callback

import net.milosvasic.factory.mail.log

class DefaultApplicationCallback<T> : FlowCallback<T> {

    override fun onFinish(success: Boolean, message: String, data: T?) {

        if (success) {
            log.v("Flow finished")
            data?.let {
                log.v("data=('$data')")
            }
        } else {
            log.w(message)
        }
    }
}