package net.milosvasic.factory.common

class CollectionWrapper<T>(val collection: T) {

    fun get(): T {
        return collection
    }
}