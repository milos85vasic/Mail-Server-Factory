package net.milosvasic.factory.common.initialization

import net.milosvasic.factory.common.Notifying
import net.milosvasic.factory.common.Subscription
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

interface Initializer : Initialization, Subscription<OperationResultListener>, Notifying<OperationResult>