package net.milosvasic.factory.mail.application

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationHandler
import net.milosvasic.factory.mail.fail
import kotlin.system.exitProcess

class DefaultInitializationHandler : InitializationHandler {

    override fun onInitialization(initializer: Initializer, success: Boolean) {
        if (!success) {
            fail(ERROR.INITIALIZATION_FAILURE)
        }
    }

    override fun onTermination(initializer: Initializer, success: Boolean) {
        if (success) {
            exitProcess(0)
        } else {
            fail(ERROR.TERMINATION_FAILURE)
        }
    }
}