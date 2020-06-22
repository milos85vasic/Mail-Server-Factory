package net.milosvasic.factory.common

interface Notifying<in T> {

    fun notify(data: T)
}