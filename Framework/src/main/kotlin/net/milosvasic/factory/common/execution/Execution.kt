package net.milosvasic.factory.common.execution

interface Execution<T> {

    fun execute(what: T)
}