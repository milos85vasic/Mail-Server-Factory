package net.milosvasic.factory.operation.command

enum class CommandConfiguration(val configuration: String) {

    LOG_COMMAND("LOG_COMMAND"),
    OBTAIN_RESULT("OBTAIN_RESULT"),
    LOG_COMMAND_RESULT("LOG_COMMAND_RESULT");

    companion object {

        val DEFAULT = mapOf(
                LOG_COMMAND to true,
                LOG_COMMAND_RESULT to true,
                OBTAIN_RESULT to false
        )

        val ALL_ON = mapOf(
                LOG_COMMAND to true,
                LOG_COMMAND_RESULT to true,
                OBTAIN_RESULT to true
        )

        val ALL_OFF = mapOf(
                LOG_COMMAND to false,
                LOG_COMMAND_RESULT to false,
                OBTAIN_RESULT to false
        )

        val MUTED = mapOf(
                LOG_COMMAND to false,
                LOG_COMMAND_RESULT to false,
                OBTAIN_RESULT to true
        )
    }
}