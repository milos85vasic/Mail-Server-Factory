package net.milosvasic.factory.mail.validation.parameters

class NoParameterValidation<T> : ParametersValidation<T> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: T): Boolean {

        if (what.isNotEmpty()) {
            throw IllegalArgumentException("Expected no arguments")
        }
        return true
    }
}