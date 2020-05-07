package net.milosvasic.factory.mail.execution.flow.callback

interface FlowCallback<T> {

    fun onFinish(success: Boolean, message: String, data: T? = null)
}