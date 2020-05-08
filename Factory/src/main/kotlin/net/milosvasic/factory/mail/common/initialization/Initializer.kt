package net.milosvasic.factory.mail.common.initialization

import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.operation.OperationResultListener

interface Initializer : Initialization, Subscription<OperationResultListener>