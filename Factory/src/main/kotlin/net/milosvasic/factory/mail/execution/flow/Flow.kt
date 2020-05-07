package net.milosvasic.factory.mail.execution.flow

interface Flow<T, M, D> : Runnable {

    fun width(subject: T): Flow<T, M, D>

    fun perform(what: M): Flow<T, M, D>

    fun onFinish(callback: FlowCallback<D>): Flow<T, M, D>
}