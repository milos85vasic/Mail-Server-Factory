package net.milosvasic.factory.mail.execution.flow.implementation

interface InitializationHandler {

    fun onInitialization(success: Boolean)

    fun onTermination(success: Boolean)
}