package net.milosvasic.factory.mail.validation.parameters

class SingleParameterValidation<T> : ParametersValidation<T> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: T): Boolean {

        if (what.size > 1 || what.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        return true
    }
}