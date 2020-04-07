package net.milosvasic.factory.mail.common

interface ObtainParametrized<in T, out M> {

    fun obtain(vararg param: T): M
}