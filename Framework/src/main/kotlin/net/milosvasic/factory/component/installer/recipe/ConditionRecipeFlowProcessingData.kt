package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.execution.flow.processing.FlowProcessingData

data class ConditionRecipeFlowProcessingData(val fallThrough: Boolean) : FlowProcessingData(boolValue = fallThrough)