package net.milosvasic.factory.execution.flow.callback

import net.milosvasic.factory.error.ERROR
import net.milosvasic.factory.fail
import net.milosvasic.factory.log

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