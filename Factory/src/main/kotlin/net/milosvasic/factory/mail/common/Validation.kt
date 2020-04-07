package net.milosvasic.factory.mail.common

interface Validation<T> {

    fun validate(vararg what: T): Boolean
}