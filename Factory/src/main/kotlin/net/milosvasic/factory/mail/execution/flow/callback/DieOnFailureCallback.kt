package net.milosvasic.factory.mail.execution.flow.callback

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log

open class DieOnFailureCallback<T> : FlowCallback<T> {

    override fun onFinish(success: Boolean, message: String, data: T?) {
        if (!success) {
            if (message != String.EMPTY) {
                die(message)
            } else {
                die("Flow execution failed")
            }
        }
    }

    protected fun die(message: String) {
        log.e(message)
        fail(ERROR.RUNTIME_ERROR)
    }
}