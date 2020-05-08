package net.milosvasic.factory.mail.execution.flow.processing

interface ProcessingRecipe {

    fun process(callback: FlowProcessingCallback)
}