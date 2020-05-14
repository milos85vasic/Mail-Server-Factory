package net.milosvasic.factory.mail.component.installer.step.condition

import net.milosvasic.factory.mail.operation.Operation

open class SkipConditionOperation(val result: Boolean) : Operation()