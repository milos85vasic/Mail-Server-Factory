package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.operation.OperationResultListener

data class DatabaseRegistration(
        val name: String,
        val database: Database,
        val callback: OperationResultListener
)