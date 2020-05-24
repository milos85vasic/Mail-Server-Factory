package net.milosvasic.factory.mail.os

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Validation
import net.milosvasic.factory.mail.validation.Validator

class HostNameValidator : Validation<String> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: String): Boolean {

        Validator.Arguments.validateSingle(*what)
        val hostname = what[0]
        if (hostname == String.EMPTY) {
            throw IllegalArgumentException("Empty hostname")
        }
        return true
    }
}