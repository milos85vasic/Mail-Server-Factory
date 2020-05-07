package net.milosvasic.factory.mail.execution.flow

interface FlowCallback<T> {

    fun onFinish(success: Boolean, message: String, data: T? = null)
}