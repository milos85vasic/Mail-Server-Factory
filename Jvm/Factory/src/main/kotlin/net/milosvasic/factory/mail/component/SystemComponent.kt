package net.milosvasic.factory.mail.component

import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.component.installer.Installation
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.component.packaging.Dependency
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

abstract class SystemComponent :
    Component(),
    Installation,
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Dependency {

    protected val busy = Busy()
}