package net.milosvasic.factory.mail.component

import net.milosvasic.factory.mail.common.Busy
import net.milosvasic.factory.mail.common.Installation
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

abstract class SystemComponent :
    Component(),
    Installation,
    Subscription<OperationResultListener>,
    Notifying<OperationResult> {

    protected val busy = Busy()
}