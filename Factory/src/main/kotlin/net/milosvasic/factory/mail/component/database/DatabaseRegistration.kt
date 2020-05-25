package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.operation.OperationResultListener

data class DatabaseRegistration(
        val database: Database,
        val callback: OperationResultListener
)