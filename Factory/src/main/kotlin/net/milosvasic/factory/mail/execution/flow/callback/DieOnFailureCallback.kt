package net.milosvasic.factory.mail.execution.flow.callback

import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import java.lang.Exception

open class DieOnFailureCallback : FlowCallback {

    override fun onFinish(success: Boolean) {
        if (!success) {
            die()
        }
    }

    protected fun die(e: Exception? = null) {

        e?.let {
            log.e(it)
        }
        fail(ERROR.RUNTIME_ERROR)
    }
}