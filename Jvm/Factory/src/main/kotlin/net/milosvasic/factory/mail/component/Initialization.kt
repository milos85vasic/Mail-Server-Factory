package net.milosvasic.factory.mail.component

interface Initialization {

    fun initialize()

    @Throws(IllegalStateException::class)
    fun checkInitialized()

    @Throws(IllegalStateException::class)
    fun checkNotInitialized()
}