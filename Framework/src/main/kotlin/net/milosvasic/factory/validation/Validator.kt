package net.milosvasic.factory.validation

import net.milosvasic.factory.validation.parameters.NoArgumentsExpectedException
import net.milosvasic.factory.validation.parameters.NoParameterValidation
import net.milosvasic.factory.validation.parameters.SingleParameterExpectedException
import net.milosvasic.factory.validation.parameters.SingleParameterValidation

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