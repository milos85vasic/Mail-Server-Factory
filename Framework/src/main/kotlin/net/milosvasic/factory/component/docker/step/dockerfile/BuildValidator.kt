package net.milosvasic.factory.component.docker.step.dockerfile

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator

class BuildValidator : Validation<String> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: String): Boolean {

        Validator.Arguments.validateSingle(*what)
        val argument = what[0]
        if (argument.split(":").size == 3) {
            return true
        }
        return false
    }
}