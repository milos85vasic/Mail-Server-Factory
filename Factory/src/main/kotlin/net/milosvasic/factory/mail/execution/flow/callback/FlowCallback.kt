package net.milosvasic.factory.mail.execution.flow.callback

interface FlowCallback {

    fun onFinish(success: Boolean, message: String)
}