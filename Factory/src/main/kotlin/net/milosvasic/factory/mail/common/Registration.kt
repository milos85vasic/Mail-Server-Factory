package net.milosvasic.factory.mail.common

interface Registration<T> {

    fun register(what: T)

    fun unRegister(what: T)
}