package net.milosvasic.factory.execution.flow.implementation.initialization

import net.milosvasic.factory.common.initialization.Initializer


interface InitializationHandler {

    fun onInitialization(initializer: Initializer, success: Boolean)

    fun onTermination(initializer: Initializer, success: Boolean)
}