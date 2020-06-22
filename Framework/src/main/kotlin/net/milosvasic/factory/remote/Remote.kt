package net.milosvasic.factory.remote

import com.google.gson.annotations.SerializedName
import net.milosvasic.factory.localhost

data class Remote(
    val host: String = localhost,
    val port: Int,
    @SerializedName("user") val account: String
)