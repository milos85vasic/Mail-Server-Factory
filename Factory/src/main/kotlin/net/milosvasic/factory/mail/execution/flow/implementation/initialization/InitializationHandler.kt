package net.milosvasic.factory.mail.execution.flow.implementation.initialization

import net.milosvasic.factory.mail.common.initialization.Initializer


interface InitializationHandler {

    fun onInitialization(initializer: Initializer, success: Boolean)

    fun onTermination(initializer: Initializer, success: Boolean)
}