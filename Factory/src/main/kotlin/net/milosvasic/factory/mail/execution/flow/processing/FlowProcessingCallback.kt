package net.milosvasic.factory.mail.execution.flow.processing

import net.milosvasic.factory.mail.EMPTY

interface FlowProcessingCallback {

    fun onFinish(success: Boolean, message: String = String.EMPTY, data: FlowProcessingData? = null)
}