package net.milosvasic.factory.mail.common

interface Subscription<T> {

    fun subscribe(what: T)

    fun unsubscribe(what: T)
}