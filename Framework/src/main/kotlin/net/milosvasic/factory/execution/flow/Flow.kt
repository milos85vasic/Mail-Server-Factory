package net.milosvasic.factory.execution.flow

import net.milosvasic.factory.execution.flow.callback.FlowCallback

interface Flow<T, D> : Runnable {

    fun width(subject: T): Flow<T, D>

    fun onFinish(callback: FlowCallback): Flow<T, D>
}