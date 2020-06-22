package net.milosvasic.factory.component.installer.step.database

import com.google.gson.annotations.SerializedName

data class DatabaseConfiguration(

        @SerializedName("NAME")
        val name: String,

        @SerializedName("TYPE")
        val type: String,

        @SerializedName("HOST")
        val host: String,

        @SerializedName("PORT")
        private val port: String,

        @SerializedName("USER")
        val user: String,

        @SerializedName("PASSWORD")
        val password: String,

        @SerializedName("SQL")
        val sqls: List<String>
) {

    @Throws(IllegalArgumentException::class)
    fun getPort(): Int = port.toInt()
}