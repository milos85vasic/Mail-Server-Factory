package net.milosvasic.factory.mail.error

enum class ERROR(
    val code: Int,
    val message: String
) {

    EMPTY_DATA(1, "No input data provided"),
    INVALID_DATA(3, "Invalid data provided"),
    FILE_DOES_NOT_EXIST(3, "File does not exist"),
    FATAL_EXCEPTION(4, "Fatal exception"),
    INITIALIZATION_FAILURE(5, "Initialization failure"),
    INSTALLATION_FAILURE(6, "Installation failure")
}