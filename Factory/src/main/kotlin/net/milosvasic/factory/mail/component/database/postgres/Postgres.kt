package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.database.Database
import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.component.database.Type
import net.milosvasic.factory.mail.component.installer.recipe.registration.MainRecipeRegistrar
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow

class Postgres(name: String, val connection: DatabaseConnection) : Database(name, connection) {

    override val type: Type
        get() = Type.Postgres

    override fun initialization() {

        initialized.set(true)
        onSuccessResult()
    }

    override fun termination() = initialized.set(false)

    override fun getInstallation(): InstallationStepFlow {

        val toolkit = Toolkit(connection.entryPoint)
        val flow = InstallationStepFlow(toolkit)
        val recipeRegistrar = MainRecipeRegistrar()
        val checkDb = SkipCondition(PostgresDatabaseCheckCommand(this))

        recipeRegistrar.registerRecipes(checkDb, flow)

        return flow.width(checkDb)
    }
}