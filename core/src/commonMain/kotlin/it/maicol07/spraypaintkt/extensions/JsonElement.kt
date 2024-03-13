package it.maicol07.spraypaintkt.extensions

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

typealias JsonObjectMap = Map<String, Any>
typealias JsonArrayList = List<JsonElement>

/**
 * Extracts the content of a [JsonElement] to a type that can be used in Kotlin.
 *
 * @return
 * - [Map]<[String], [JsonElement]> if the [JsonElement] is a [JsonObject]
 * - [List]<[Any]> if the [JsonElement] is a [JsonArray]
 * - [String], [Boolean], [Int], [Long], [Float], [Double] if the [JsonElement] is a [JsonPrimitive]
 * - `null` if the [JsonElement] is not a [JsonObject], [JsonArray] or [JsonPrimitive]
 *
 * @source https://github.com/Kotlin/kotlinx.serialization/issues/1537#issuecomment-1315827235
 */
val JsonElement.extractedContent: Any?
    get() {
        if (this is JsonPrimitive) {
            if (this.jsonPrimitive.isString) {
                return this.jsonPrimitive.content
            }
            return this.jsonPrimitive.booleanOrNull ?: this.jsonPrimitive.intOrNull
            ?: this.jsonPrimitive.longOrNull ?: this.jsonPrimitive.floatOrNull
            ?: this.jsonPrimitive.doubleOrNull ?: this.jsonPrimitive.contentOrNull
        }
        if (this is JsonArray) {
            return this.jsonArray.map {
                it.extractedContent
            }
        }
        if (this is JsonObject) {
            return this.jsonObject.entries.associate {
                it.key to it.value.extractedContent
            }
        }
        return null
    }

fun Any?.toJsonElement(): JsonElement = when(this) {
    null -> JsonNull
    is Map<*, *> -> toJsonElement()
    is Collection<*> -> toJsonElement()
    is ByteArray -> this.toList().toJsonElement()
    is CharArray -> this.toList().toJsonElement()
    is ShortArray -> this.toList().toJsonElement()
    is IntArray -> this.toList().toJsonElement()
    is LongArray -> this.toList().toJsonElement()
    is FloatArray -> this.toList().toJsonElement()
    is DoubleArray -> this.toList().toJsonElement()
    is BooleanArray -> this.toList().toJsonElement()
    is Array<*> -> toJsonElement()
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Enum<*> -> JsonPrimitive(this.toString())
    else -> {
        throw IllegalStateException("Can't serialize unknown type: $this")
    }
}

fun  Map<*, *>.toJsonElement(): JsonElement {
    val map = mutableMapOf<String, JsonElement>()
    for ((key, value) in this) {
        key as String
        map[key] = value.toJsonElement()
    }
    return JsonObject(map)
}

fun Collection<*>.toJsonElement(): JsonElement {
    return JsonArray(this.map { it.toJsonElement() })
}

fun Array<*>.toJsonElement(): JsonElement {
    return JsonArray(this.map { it.toJsonElement() })
}