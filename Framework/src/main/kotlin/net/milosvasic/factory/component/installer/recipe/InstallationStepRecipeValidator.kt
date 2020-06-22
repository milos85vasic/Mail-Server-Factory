package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator

class InstallationStepRecipeValidator : Validation<InstallationStepRecipe> {

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun validate(vararg what: InstallationStepRecipe): Boolean {

        Validator.Arguments.validateSingle(what)
        val recipe = what[0]
        if (recipe.obtainToolkit() == null) {
            throw IllegalStateException("Toolkit point is not provided")
        }
        if (recipe.obtainInstallationStep() == null) {
            throw IllegalStateException("Installation step is not provided")
        }
        return true
    }
}