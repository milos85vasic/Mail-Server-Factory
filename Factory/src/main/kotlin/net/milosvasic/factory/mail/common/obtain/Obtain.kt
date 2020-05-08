package net.milosvasic.factory.mail.common.obtain

interface Obtain<out T> {

    fun obtain(): T
}