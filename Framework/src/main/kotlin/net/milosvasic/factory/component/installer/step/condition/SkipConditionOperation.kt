package net.milosvasic.factory.component.installer.step.condition

import net.milosvasic.factory.operation.Operation

open class SkipConditionOperation(val result: Boolean) : Operation()