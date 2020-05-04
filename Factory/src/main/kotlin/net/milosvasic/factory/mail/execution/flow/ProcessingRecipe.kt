package net.milosvasic.factory.mail.execution.flow

interface ProcessingRecipe {

    fun process(callback: FlowProcessingCallback)
}