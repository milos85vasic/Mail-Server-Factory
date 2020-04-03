package net.milosvasic.factory.mail.error

enum class ERROR(
    val code: Int,
    val message: String
) {

    EMPTY_DATA(1, "No input data provided"),
    FILE_DOES_NOT_EXIST(2, "File does not exist"),
    FATAL_EXCEPTION(3, "Fatal exception"),
    INITIALIZATION_FAILURE(4, "Initialization failure"),
    INSTALLATION_FAILURE(5, "Installation failure")
}