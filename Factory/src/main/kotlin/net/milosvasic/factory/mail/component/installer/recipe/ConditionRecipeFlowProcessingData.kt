package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingData

data class ConditionRecipeFlowProcessingData(val skipSection: Boolean) :
        FlowProcessingData(boolValue = skipSection)