package net.milosvasic.factory.mail.common

interface Execution<T> {

    fun execute(what: T)
}