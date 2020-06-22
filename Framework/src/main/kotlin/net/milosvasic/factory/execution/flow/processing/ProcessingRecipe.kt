package net.milosvasic.factory.execution.flow.processing

interface ProcessingRecipe {

    fun process(callback: FlowProcessingCallback)
}