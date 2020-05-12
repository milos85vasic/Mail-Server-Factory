package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.common.Validation
import net.milosvasic.factory.mail.validation.Validator

class InstallationStepRecipeValidator : Validation<InstallationStepRecipe<*>> {

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun validate(vararg what: InstallationStepRecipe<*>): Boolean {

        Validator.Arguments.validateSingle(what)
        val recipe = what[0]
        if (recipe.obtainEntryPoint() == null) {
            throw IllegalStateException("Entry point is not provided")
        }
        if (recipe.obtainInstallationStep() == null) {
            throw IllegalStateException("Installation step is not provided")
        }
        return true
    }
}