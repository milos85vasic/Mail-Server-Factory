package net.milosvasic.factory.component.installer.step.port

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator

class PortCheckValidator : Validation<String> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: String): Boolean {

        Validator.Arguments.validateSingle(what)
        val arg = what[0]
        if (arg == String.EMPTY) {
            throw IllegalArgumentException("Empty port check parameter")
        }
        val delimiter = PortCheck.delimiter
        if (arg.contains(delimiter)) {
            val split = arg.split(PortCheck.delimiter)
            split.forEach {
                it.trim().toInt()
            }
        } else {
            arg.toInt()
        }
        return true
    }
}