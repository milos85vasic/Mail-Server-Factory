package net.milosvasic.factory.mail.execution.flow

interface FlowCallback {

    fun onFinish(success: Boolean, message: String)
}