package net.milosvasic.factory.mail.common.execution

interface Execution<T> {

    fun execute(what: T)
}