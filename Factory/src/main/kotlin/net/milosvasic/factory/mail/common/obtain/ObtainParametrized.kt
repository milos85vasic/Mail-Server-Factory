package net.milosvasic.factory.mail.common.obtain

interface ObtainParametrized<in T, out M> {

    fun obtain(vararg param: T): M
}