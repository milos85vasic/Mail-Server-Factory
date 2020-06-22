package net.milosvasic.factory.validation.parameters

class NoParameterValidation<T> : ParametersValidation<T> {

    @Throws(NoArgumentsExpectedException::class)
    override fun validate(vararg what: T): Boolean {

        if (what.isNotEmpty()) {
            throw NoArgumentsExpectedException()
        }
        return true
    }
}