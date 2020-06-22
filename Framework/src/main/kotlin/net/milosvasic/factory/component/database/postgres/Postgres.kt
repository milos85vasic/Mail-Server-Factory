package net.milosvasic.factory.component.database.postgres

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.database.Database
import net.milosvasic.factory.component.database.DatabaseConnection
import net.milosvasic.factory.component.database.Type
import net.milosvasic.factory.component.installer.recipe.registration.MainRecipeRegistrar
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow

class Postgres(name: String, connection: DatabaseConnection) : Database(name, connection) {

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

        val steps = listOf<InstallationStep<*>>(
                SkipCondition(checkCommand()),
                CommandInstallationStep(createCommand()),
                CommandInstallationStep(checkCommand())
        )

        steps.forEach {
            recipeRegistrar.registerRecipes(it, flow)
            flow.width(it)
        }

        return flow
    }

    override fun getSqlCommand(sql: String) = PostgresDatabaseSqlCommand(this, sql)

    private fun checkCommand() = PostgresDatabaseCheckCommand(this)

    private fun createCommand() = PostgresDatabaseCreateCommand(this)
}