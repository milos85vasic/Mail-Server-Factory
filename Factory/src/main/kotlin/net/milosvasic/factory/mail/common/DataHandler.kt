package net.milosvasic.factory.mail.common

interface DataHandler<T> {

    fun onData(data: T?)
}