package net.milosvasic.factory.validation.parameters

class SingleParameterValidation<T> : ParametersValidation<T> {

    @Throws(SingleParameterExpectedException::class)
    override fun validate(vararg what: T): Boolean {

        if (what.size > 1 || what.isEmpty()) {
            throw SingleParameterExpectedException()
        }
        return true
    }
}