package net.milosvasic.factory.mail.service

data class Service(
    private val type: String,
    val name: String
) {

    fun getType(): TYPE = TYPE.getByValue(type)
}