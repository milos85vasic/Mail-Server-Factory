package net.milosvasic.factory.application

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.common.exception.EmptyDataException
import net.milosvasic.factory.validation.Validator

class ArgumentsValidator  : Validation<Array<String>> {

    @Throws(EmptyDataException::class, IllegalArgumentException::class)
    override fun validate(vararg what: Array<String>): Boolean {

        Validator.Arguments.validateSingle(what)
        val args = what[0]
        if (args.isEmpty()) {
            throw EmptyDataException()
        }
        return true
    }
}