package net.milosvasic.factory.mail.execution.flow.callback

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.initialization.Termination

class TerminationCallback(private val termination: Termination) : DieOnFailureCallback() {

    override fun onFinish(success: Boolean, message: String) {
        super.onFinish(success, message)
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