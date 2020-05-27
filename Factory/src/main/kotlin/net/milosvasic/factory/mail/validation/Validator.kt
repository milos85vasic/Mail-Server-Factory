package net.milosvasic.factory.mail.validation

import net.milosvasic.factory.mail.validation.parameters.NoParameterValidation
import net.milosvasic.factory.mail.validation.parameters.SingleParameterValidation

object Validator {

    object Arguments {

        @Throws(IllegalArgumentException::class)
        fun validateSingle(vararg params: Any) {

            val validation = SingleParameterValidation<Any>()
            validation.validate(*params)
        }

        @Throws(IllegalArgumentException::class)
        fun validateEmpty(vararg params: Any) {

            val validation = NoParameterValidation<Any>()
            validation.validate(*params)
        }
    }
}