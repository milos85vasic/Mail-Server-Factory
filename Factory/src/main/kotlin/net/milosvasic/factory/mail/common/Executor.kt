package net.milosvasic.factory.mail.common

import net.milosvasic.factory.mail.operation.OperationResultListener

interface Executor<T> : Execution<T>, Subscription<OperationResultListener>