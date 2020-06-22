package net.milosvasic.factory.common.busy

import java.util.concurrent.atomic.AtomicBoolean

class Busy {

    private val busy = AtomicBoolean()

    @Synchronized
    fun isBusy(): Boolean = busy.get()

    @Synchronized
    fun setBusy(busy: Boolean) {
        this.busy.set(busy)
    }
}