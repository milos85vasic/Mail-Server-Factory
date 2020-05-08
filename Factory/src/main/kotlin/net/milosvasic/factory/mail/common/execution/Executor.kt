package net.milosvasic.factory.mail.common.execution

import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.operation.OperationResultListener

interface Executor<T> : Execution<T>, Subscription<OperationResultListener>