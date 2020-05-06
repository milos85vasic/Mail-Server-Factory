package net.milosvasic.factory.mail.execution.flow

interface Flow<T, M> : Runnable {

    fun width(subject: T): Flow<T, M>

    fun perform(what: M): Flow<T, M>

    fun onFinish(callback: FlowCallback): Flow<T, M>
}