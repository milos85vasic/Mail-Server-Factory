package net.milosvasic.factory.mail.common.busy

interface BusyDelegationParametrized<in T> {

    fun busy(what: T)

    fun free(what: T)
}