package net.milosvasic.factory.configuration

import com.google.gson.*
import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.GsonDeserialization
import java.lang.reflect.Type

data class VariableNode(
        val name: String = String.EMPTY,
        val value: Any = String.EMPTY,
        val children: MutableList<VariableNode> = mutableListOf()
) {

    companion object : GsonDeserialization<VariableNode> {

        const val contextSeparator = "."

        override fun getDeserializer(): JsonDeserializer<VariableNode> {
            return object : JsonDeserializer<VariableNode> {

                override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                ): VariableNode {

                    if (json == null) {
                        throw JsonParseException("JSON is null")
                    }
                    when (json) {
                        is JsonObject -> {
                            val name = String.EMPTY
                            val entrySet = json.entrySet()
                            val children = mutableListOf<VariableNode>()
                            entrySet.forEach { item ->

                                val itemName = item.key
                                val itemValue = item.value
                                if (itemName == String.EMPTY) {
                                    throw JsonParseException("Empty key")
                                }
                                when (item.value) {
                                    is JsonObject -> {
                                        val itemObjectValue = itemValue.asJsonObject
                                        val child = processJsonObject(itemName, itemObjectValue)
                                        children.add(child)
                                    }
                                    is JsonPrimitive -> {
                                        val itemPrimitiveValue = itemValue.asJsonPrimitive
                                        val value: Any = when {
                                            itemPrimitiveValue.isBoolean -> {
                                                itemValue.asBoolean
                                            }
                                            itemPrimitiveValue.isNumber -> {
                                                itemValue.asFloat
                                            }
                                            else -> {
                                                itemValue.asString
                                            }
                                        }
                                        val child = VariableNode(
                                                name = itemName,
                                                value = value
                                        )
                                        children.add(child)
                                    }
                                }
                            }
                            return VariableNode(
                                    name = name,
                                    children = children
                            )
                        }
                        else -> {
                            throw JsonParseException("Unexpected JSON element: ${json::class.simpleName}")
                        }
                    }
                }
            }
        }

        @Throws(JsonParseException::class)
        private fun processJsonObject(parent: String, jsonObject: JsonObject): VariableNode {
            if (jsonObject.keySet().isEmpty()) {
                throw JsonParseException("No keys")
            } else {
                val entrySet = jsonObject.entrySet()
                val children = mutableListOf<VariableNode>()
                entrySet.forEach { item ->
                    val itemKey = item.key
                    when (val itemValue = item.value) {
                        is JsonObject -> {
                            val child = processJsonObject(itemKey, itemValue)
                            children.add(child)
                        }
                        is JsonPrimitive -> {
                            var value: Any = String.EMPTY
                            when {
                                itemValue.isString -> {
                                    value = itemValue.asString
                                }
                                itemValue.isBoolean -> {
                                    value = itemValue.asBoolean
                                }
                                itemValue.isNumber -> {
                                    value = itemValue.asFloat
                                }
                            }
                            val child = VariableNode(
                                    name = itemKey,
                                    value = value
                            )
                            children.add(child)
                        }
                        else -> {
                            throw JsonParseException("Unsupported structure member: ${itemValue::class.simpleName}")
                        }
                    }
                }
                return VariableNode(
                        name = parent,
                        children = children
                )
            }
        }
    }

    fun append(vararg nodes: VariableNode): VariableNode {

        nodes.forEach { node ->
            if (node.children.isNotEmpty()) {
                if (node.name != String.EMPTY) {
                    var added = false
                    children.forEach { child ->
                        if (child.name == node.name) {
                            node.children.forEach { nodeChild ->
                                child.append(nodeChild)
                            }
                            added = true
                        }
                    }
                    if (!added) {
                        children.add(node)
                    }
                } else {
                    children.addAll(node.children)
                }
            }
            if (node.value != String.EMPTY) {
                children.add(node)
            }
        }
        return this
    }

    fun get(what: String): String? {

        val path = what.split(contextSeparator)
        val iterator = path.iterator()
        var node = this
        while (iterator.hasNext()) {
            val position = iterator.next()
            node.children.forEach { child ->
                if (child.name == position.trim()) {
                    node = child
                }
            }
            if (node == this) {
                return null
            }
        }
        return node.value.toString()
    }
}