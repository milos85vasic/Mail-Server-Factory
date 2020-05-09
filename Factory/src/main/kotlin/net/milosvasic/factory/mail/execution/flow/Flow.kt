package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback

interface Flow<T, D> : Runnable {

    fun width(subject: T): Flow<T, D>

    fun onFinish(callback: FlowCallback<D>): Flow<T, D>

}