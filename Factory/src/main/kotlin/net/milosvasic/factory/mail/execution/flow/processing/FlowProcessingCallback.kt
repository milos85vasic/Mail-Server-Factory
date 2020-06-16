package net.milosvasic.factory.mail.execution.flow.processing

interface FlowProcessingCallback {

    fun onFinish(success: Boolean, data: FlowProcessingData? = null)
}