package net.milosvasic.factory.common

interface DataHandler<T> {

    fun onData(data: T?)
}