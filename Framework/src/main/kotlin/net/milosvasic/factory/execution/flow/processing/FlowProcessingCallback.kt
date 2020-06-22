package net.milosvasic.factory.execution.flow.processing

interface FlowProcessingCallback {

    fun onFinish(success: Boolean, data: FlowProcessingData? = null)
}