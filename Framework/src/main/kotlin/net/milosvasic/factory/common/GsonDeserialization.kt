package net.milosvasic.factory.common

import com.google.gson.JsonDeserializer

interface GsonDeserialization<T> {

    fun getDeserializer(): JsonDeserializer<T>
}