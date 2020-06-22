package net.milosvasic.factory.common.busy

interface BusyDelegationParametrized<in T> {

    fun busy(what: T)

    fun free(what: T)
}