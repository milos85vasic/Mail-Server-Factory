package net.milosvasic.factory.mail.execution.flow.callback

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.initialization.Termination
import java.lang.Exception

class TerminationCallback<T>(private val termination: Termination) : DieOnFailureCallback<T>() {

    override fun onFinish(success: Boolean, message: String, data: T?) {
        super.onFinish(success, message, data)
        if (success) {
            try {
                termination.terminate()
            } catch (e: Exception) {

                var eMessage = String.EMPTY
                e::class.simpleName?.let {
                    eMessage = it
                }
                e.message?.let {
                    eMessage = it
                }
                die(eMessage)
            }
        }
    }
}