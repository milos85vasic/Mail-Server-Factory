package net.milosvasic.factory.mail.execution.flow

interface FlowPerform<T, M, D> {

    fun perform(what: M): Flow<T, M, D>
}