package net.milosvasic.factory.common

interface Registration<T> {

    fun register(what: T)

    fun unRegister(what: T)
}