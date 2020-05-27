package net.milosvasic.factory.mail.configuration

enum class VariableKey(val key: String) {

    HOSTNAME("HOSTNAME"),
    SERVER_HOME("SERVER_HOME"),
    REBOOT_ALLOWED("REBOOT_ALLOWED"),
    DOCKER_HOME("DOCKER_HOME"),
    DOCKER_COMPOSE_PATH("DOCKER_COMPOSE_PATH"),
    DB_PORT("DB_PORT"),
    DB_PASSWORD("DB_PASSWORD")
}