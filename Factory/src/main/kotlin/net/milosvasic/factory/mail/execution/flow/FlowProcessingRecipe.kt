package net.milosvasic.factory.mail.execution.flow

interface FlowProcessingRecipe<T, M> {

    fun process(subject: T, operation: M, callback: FlowProcessingCallback)
}