package net.milosvasic.factory.mail.common.busy

interface BusyDelegation {

    fun busy()

    fun free()
}