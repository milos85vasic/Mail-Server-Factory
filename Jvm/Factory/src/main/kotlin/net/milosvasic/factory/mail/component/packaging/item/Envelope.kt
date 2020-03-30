package net.milosvasic.factory.mail.component.packaging.item

import net.milosvasic.factory.mail.EMPTY

class Envelope(private vararg val packages: String) {

    fun getValue(): String {
        var value = String.EMPTY
        packages.forEach {
            value += "$it "
        }
        return value.trim()
    }
}