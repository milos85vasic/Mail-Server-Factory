package net.milosvasic.factory.mail.component

import kotlin.reflect.KClass

object ComponentManager {

    private var index = 0
    private val components = mutableMapOf<KClass<*>, Int>()

    fun subscribe(what: KClass<*>): Int {
        if (components.keys.contains(what)) {
            components[what]?.let {
                return it
            }
        } else {
            index++
            components[what] = index
            return index
        }
        return -1
    }

    fun unsubscribe(what: KClass<*>) {
        components.remove(what)
    }
}