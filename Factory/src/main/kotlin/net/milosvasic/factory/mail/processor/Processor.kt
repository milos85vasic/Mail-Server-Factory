package net.milosvasic.factory.mail.processor

interface Processor<in T> {

    fun process(what: T)
}