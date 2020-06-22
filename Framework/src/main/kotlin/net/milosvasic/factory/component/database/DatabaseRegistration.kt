package net.milosvasic.factory.component.database

import net.milosvasic.factory.operation.OperationResultListener

data class DatabaseRegistration(
        val database: Database,
        val callback: OperationResultListener
)