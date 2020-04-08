package net.milosvasic.factory.mail.common

interface Build<T> {

    fun build(): T
}