package net.milosvasic.factory.common.execution

import net.milosvasic.factory.common.Notifying
import net.milosvasic.factory.common.Subscription
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

interface Executor<T> : Execution<T>, Subscription<OperationResultListener>, Notifying<OperationResult>