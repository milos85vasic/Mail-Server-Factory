package net.milosvasic.factory.mail.component

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object ComponentManager {

    private var index = AtomicInteger()
    private val components = ConcurrentHashMap<KClass<*>, Int>()

    @Synchronized
    fun subscribe(what: KClass<*>): Int {
        if (components.keys.contains(what)) {
            components[what]?.let {
                return it
            }
        } else {
            val idx = index.incrementAndGet()
            components[what] = idx
            return idx
        }
        return -1
    }

    @Synchronized
    fun unsubscribe(what: KClass<*>) {
        components.remove(what)
    }
}