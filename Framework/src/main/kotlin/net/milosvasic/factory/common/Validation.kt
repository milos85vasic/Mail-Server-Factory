package net.milosvasic.factory.common

interface Validation<T> {

    fun validate(vararg what: T): Boolean
}