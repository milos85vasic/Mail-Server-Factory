package net.milosvasic.factory.mail.common

interface Notifying<in T> {

    fun notify(data: T)
}