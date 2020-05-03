package net.milosvasic.factory.mail.common

import com.google.gson.JsonDeserializer

interface GsonDeserialization<T> {

    fun getDeserializer(): JsonDeserializer<T>
}