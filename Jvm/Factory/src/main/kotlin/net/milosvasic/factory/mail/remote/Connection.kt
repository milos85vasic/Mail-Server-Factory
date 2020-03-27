package net.milosvasic.factory.mail.remote

abstract class Connection<T : Remote>(private val remote: T) {



    abstract fun test()
}