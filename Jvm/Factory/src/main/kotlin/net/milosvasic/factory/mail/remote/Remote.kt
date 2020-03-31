package net.milosvasic.factory.mail.remote

import com.google.gson.annotations.SerializedName
import net.milosvasic.factory.mail.localhost

data class Remote(
    val host: String = localhost,
    val port: Int,
    @SerializedName("user") val account: String
)