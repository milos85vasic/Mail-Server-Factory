package net.milosvasic.factory.common.obtain

interface Obtain<out T> {

    fun obtain(): T
}