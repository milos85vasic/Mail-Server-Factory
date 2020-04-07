package net.milosvasic.factory.mail.common

interface Obtain<out T> {

    fun obtain(): T
}