package net.milosvasic.factory.common.initialization

interface Initialization {

    fun initialize()

    fun isInitialized(): Boolean

    @Throws(IllegalStateException::class)
    fun checkInitialized()

    @Throws(IllegalStateException::class)
    fun checkNotInitialized()
}