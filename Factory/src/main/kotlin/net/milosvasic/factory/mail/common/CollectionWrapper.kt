package net.milosvasic.factory.mail.common

class CollectionWrapper<T>(val collection: T) {

    fun get(): T {
        return collection
    }
}