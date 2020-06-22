package net.milosvasic.factory.common.obtain

interface ObtainParametrized<in T, out M> {

    fun obtain(vararg param: T): M
}