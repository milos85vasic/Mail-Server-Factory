package net.milosvasic.factory.mail.application

import net.milosvasic.factory.mail.common.Validation
import net.milosvasic.factory.mail.common.exception.EmptyDataException
import net.milosvasic.factory.mail.validation.Validator

class ServerFactoryArgumentsValidator  : Validation<Array<String>> {

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