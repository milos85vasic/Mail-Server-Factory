package net.milosvasic.factory.common.busy

object BusyDelegate : BusyDelegationParametrized<Busy> {

    @Synchronized
    @Throws(BusyException::class)
    override fun busy(what: Busy) {
        if (what.isBusy()) {
            throw BusyException()
        }
        what.setBusy(true)
    }

    @Synchronized
    override fun free(what: Busy) {
        what.setBusy(false)
    }
}