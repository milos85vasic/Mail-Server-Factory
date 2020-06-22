package net.milosvasic.factory.common

interface Subscription<T> {

    fun subscribe(what: T)

    fun unsubscribe(what: T)
}