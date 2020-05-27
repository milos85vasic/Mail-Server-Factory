package net.milosvasic.factory.mail.validation

import net.milosvasic.factory.mail.validation.parameters.NoArgumentsExpectedException
import net.milosvasic.factory.mail.validation.parameters.NoParameterValidation
import net.milosvasic.factory.mail.validation.parameters.SingleParameterExpectedException
import net.milosvasic.factory.mail.validation.parameters.SingleParameterValidation

object Validator {

    object Arguments {

        @Throws(SingleParameterExpectedException::class)
        fun validateSingle(vararg params: Any) {

            val validation = SingleParameterValidation<Any>()
            validation.validate(*params)
        }

        @Throws(NoArgumentsExpectedException::class)
        fun validateEmpty(vararg params: Any) {

            val validation = NoParameterValidation<Any>()
            validation.validate(*params)
        }
    }
}