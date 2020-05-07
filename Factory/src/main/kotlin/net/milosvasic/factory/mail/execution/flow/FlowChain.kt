package net.milosvasic.factory.mail.execution.flow

interface FlowChain {

    fun chain(flow: Flow<*, *, *>): FlowChain
}