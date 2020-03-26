package net.milosvasic.factory.mail

enum class ERROR(
    val code: Int,
    val message: String
) {

    EMPTY_DATA(1, "No input data provided")
}