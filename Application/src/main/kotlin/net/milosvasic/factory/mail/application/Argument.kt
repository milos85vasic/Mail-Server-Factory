package net.milosvasic.factory.mail.application

enum class Argument(private val arg: String) {

    INSTALLATION_LOCATION("installationHome");

    companion object {

        const val ARGUMENT_PREFIX = "--"
    }

    fun get() = "$ARGUMENT_PREFIX$arg="
}