package net.milosvasic.factory.application

import net.milosvasic.factory.common.initialization.Initializer
import net.milosvasic.factory.error.ERROR
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationHandler
import net.milosvasic.factory.fail
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