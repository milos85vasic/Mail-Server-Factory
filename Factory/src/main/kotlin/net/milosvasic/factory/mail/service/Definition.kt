package net.milosvasic.factory.mail.service

data class Definition(
    private val type: String,
    val name: String
) {

    fun getType(): TYPE = TYPE.getByValue(type)
}