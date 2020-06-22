package net.milosvasic.factory.execution.flow.processing

abstract class FlowProcessingData(
        private val stringValue: String? = null,
        private val boolValue: Boolean? = null
) {

    override fun toString(): String {
        return "FlowProcessingData(stringValue=$stringValue, boolValue=$boolValue)"
    }
}