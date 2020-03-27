package net.milosvasic.factory.mail.service

enum class TYPE(
    val type: String
) {

    UNKNOWN("Unknown"),
    DOVECOT("Dovecot"),
    POSTFIX("Postfix");

    companion object {

        fun getByValue(value: String): TYPE {

            values().forEach {
                if (it.type == value) {
                    return it
                }
            }
            return UNKNOWN
        }
    }
}