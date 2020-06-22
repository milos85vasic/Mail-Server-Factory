package net.milosvasic.factory.execution.flow.callback

import net.milosvasic.factory.common.initialization.Termination

class TerminationCallback(private val termination: Termination) : DieOnFailureCallback() {

    override fun onFinish(success: Boolean) {
        super.onFinish(success)
        if (success) {
            try {
                termination.terminate()
            } catch (e: Exception) {

                die(e)
            }
        }
    }
}