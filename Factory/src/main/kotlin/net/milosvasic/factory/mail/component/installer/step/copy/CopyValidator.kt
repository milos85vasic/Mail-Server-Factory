package net.milosvasic.factory.mail.component.installer.step.copy

import net.milosvasic.factory.mail.common.Validation
import net.milosvasic.factory.mail.validation.Validator

class CopyValidator : Validation<String> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: String): Boolean {

        Validator.Arguments.validateSingle(what)
        val arg = what[0]
        val split = arg.split(Copy.delimiter)
        if (split.isEmpty()) {
            throw IllegalArgumentException("No delimited parameters available in form: 'from:to'")
        }
        if (split.size != 2) {
            throw IllegalArgumentException("No valid delimited parameters available in form: 'from:to'")
        }
        return true
    }
}