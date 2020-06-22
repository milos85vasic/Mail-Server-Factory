package net.milosvasic.factory.component.database

enum class Type(val type: String) {

    MySQL("MySQL"),
    Postgres("Postgres"),
    Unknown("Unknown");

    companion object {

        fun getType(value: String): Type {
            values().forEach {
                if (it.type == value) {
                    return it
                }
            }
            return Unknown
        }
    }
}