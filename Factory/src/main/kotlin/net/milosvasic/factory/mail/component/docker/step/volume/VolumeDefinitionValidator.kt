package net.milosvasic.factory.mail.component.docker.step.volume

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Validation

class VolumeDefinitionValidator : Validation<String> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: String): Boolean {
        if (what.isEmpty()) {

            throw IllegalArgumentException("Empty volume parameters")
        }
        var valid = false
        what.forEach {
            if (it != String.EMPTY) {
                valid = true
            }
        }
        if (!valid) {

            throw IllegalArgumentException("All volume parameters are empty no parameters at all")
        }
        return valid
    }
}