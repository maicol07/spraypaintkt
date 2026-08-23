package it.maicol07.spraypaintkt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

internal val jsonApiJson = Json { ignoreUnknownKeys = true }

/** Base fields for a JSON:API response. */
sealed interface JsonApiResponse {
    /** The top-level meta of the response. */
    val meta: Map<String, JsonElement>

    /** The top-level links of the response. */
    val links: JsonApiLinks?

    /** The included resources. */
    val included: List<JsonApiResource>

    /** Information about the server's JSON:API implementation. */
    val jsonapi: JsonApiObject?
}

/** A single JSON:API response. */
@Serializable
data class JsonApiSingleResponse(
    /** The data of the response. */
    val data: JsonApiResource? = null,
    override val meta: Map<String, JsonElement> = emptyMap(),
    override val links: JsonApiLinks? = null,
    override val included: List<JsonApiResource> = emptyList(),
    override val jsonapi: JsonApiObject? = null,
) : JsonApiResponse {
    companion object {
        fun fromJsonApiString(jsonApiString: String): JsonApiSingleResponse =
            jsonApiJson.decodeFromString(jsonApiString)
    }
}

/** A collection JSON:API response. */
@Serializable
data class JsonApiCollectionResponse(
    /** The data of the response. */
    val data: List<JsonApiResource>,
    override val meta: Map<String, JsonElement> = emptyMap(),
    override val links: JsonApiLinks? = null,
    override val included: List<JsonApiResource> = emptyList(),
    override val jsonapi: JsonApiObject? = null,
) : JsonApiResponse {
    companion object {
        fun fromJsonApiString(jsonApiString: String): JsonApiCollectionResponse =
            jsonApiJson.decodeFromString(jsonApiString)
    }
}

/** A JSON:API resource. */
@Serializable
data class JsonApiResource(
    /** The ID of the resource. */
    val id: String,
    /** The type of the resource. */
    val type: String,
    /** The attributes of the resource. */
    val attributes: Map<String, JsonElement> = emptyMap(),
    /** The relationships of the resource. */
    val relationships: Map<String, JsonApiRelationship> = emptyMap(),
    /** The links of the resource. */
    val links: JsonApiLinks? = null,
    /** The meta of the resource. */
    val meta: Map<String, JsonElement> = emptyMap(),
) {
    companion object {
        fun fromJsonApiString(jsonApiString: String): JsonApiResource =
            jsonApiJson.decodeFromString(jsonApiString)
    }
}

/** A JSON:API relationship. */
@Serializable
data class JsonApiRelationship(
    /** The linkage of the relationship. */
    val data: JsonApiLinkage = JsonApiLinkage.Missing,
    /** The links of the relationship. */
    val links: JsonApiLinks? = null,
    /** The meta of the relationship. */
    val meta: Map<String, JsonElement> = emptyMap(),
)

/** JSON:API relationship linkage. */
@Serializable(with = JsonApiLinkageSerializer::class)
sealed interface JsonApiLinkage {
    /** The relationship has no data member. */
    data object Missing : JsonApiLinkage

    /** An empty to-one relationship. */
    data object EmptyToOne : JsonApiLinkage

    /** A non-empty to-one relationship. */
    data class ToOne(val resource: JsonApiRelationshipData) : JsonApiLinkage

    /** A to-many relationship. */
    data class ToMany(val resources: List<JsonApiRelationshipData>) : JsonApiLinkage
}

/** A JSON:API relationship linkage object. */
@Serializable
data class JsonApiRelationshipData(
    /** The ID of the relationship. */
    val id: String,
    /** The type of the relationship. */
    val type: String,
)

/** Information about the server's JSON:API implementation. */
@Serializable
data class JsonApiObject(
    val version: String? = null,
    val ext: List<String> = emptyList(),
    val profile: List<String> = emptyList(),
    val meta: Map<String, JsonElement> = emptyMap(),
)

/** A JSON:API link. */
@Serializable(with = JsonApiLinkSerializer::class)
sealed interface JsonApiLink {
    val href: String

    /** A link represented by a URI-reference string. */
    data class Simple(override val href: String) : JsonApiLink

    /** A link represented by a link object. */
    @Serializable
    data class Details(
        override val href: String,
        val rel: String? = null,
        @SerialName("describedby") val describedBy: JsonApiLink? = null,
        val title: String? = null,
        val type: String? = null,
        @Serializable(with = JsonApiHreflangSerializer::class)
        val hreflang: List<String>? = null,
        val meta: Map<String, JsonElement> = emptyMap(),
    ) : JsonApiLink
}

/** A JSON:API links object. */
@Serializable
data class JsonApiLinks(
    val self: JsonApiLink? = null,
    val related: JsonApiLink? = null,
    val first: JsonApiLink? = null,
    val last: JsonApiLink? = null,
    val prev: JsonApiLink? = null,
    val next: JsonApiLink? = null,
    @SerialName("describedby") val describedBy: JsonApiLink? = null,
) {
    internal fun asMap(): Map<String, JsonApiLink> = buildMap {
        self?.let { put("self", it) }
        related?.let { put("related", it) }
        first?.let { put("first", it) }
        last?.let { put("last", it) }
        prev?.let { put("prev", it) }
        next?.let { put("next", it) }
        describedBy?.let { put("describedby", it) }
    }
}

typealias JsonApiRelationshipLinks = JsonApiLinks

internal object JsonApiLinkSerializer : KSerializer<JsonApiLink> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): JsonApiLink {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("JSON:API links support JSON serialization only")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> if (element.isString) {
                JsonApiLink.Simple(element.content)
            } else {
                throw SerializationException("JSON:API link must be a string, object or null")
            }
            is JsonObject -> jsonDecoder.json.decodeFromJsonElement<JsonApiLink.Details>(element)
            else -> throw SerializationException("JSON:API link must be a string, object or null")
        }
    }

    override fun serialize(encoder: Encoder, value: JsonApiLink) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("JSON:API links support JSON serialization only")
        val element = when (value) {
            is JsonApiLink.Simple -> JsonPrimitive(value.href)
            is JsonApiLink.Details -> jsonEncoder.json.encodeToJsonElement(JsonApiLink.Details.serializer(), value)
        }
        jsonEncoder.encodeJsonElement(element)
    }
}

internal object JsonApiLinkageSerializer : KSerializer<JsonApiLinkage> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): JsonApiLinkage {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("JSON:API linkage supports JSON serialization only")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            JsonNull -> JsonApiLinkage.EmptyToOne
            is JsonObject -> JsonApiLinkage.ToOne(jsonDecoder.json.decodeFromJsonElement(element))
            is JsonArray -> JsonApiLinkage.ToMany(element.map(jsonDecoder.json::decodeFromJsonElement))
            else -> throw SerializationException("JSON:API linkage must be null, an object or an array")
        }
    }

    override fun serialize(encoder: Encoder, value: JsonApiLinkage) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("JSON:API linkage supports JSON serialization only")
        val element = when (value) {
            JsonApiLinkage.Missing,
            JsonApiLinkage.EmptyToOne -> JsonNull
            is JsonApiLinkage.ToOne -> jsonEncoder.json.encodeToJsonElement(value.resource)
            is JsonApiLinkage.ToMany -> jsonEncoder.json.encodeToJsonElement(value.resources)
        }
        jsonEncoder.encodeJsonElement(element)
    }
}

internal object JsonApiHreflangSerializer : KSerializer<List<String>?> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<String>? {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("JSON:API hreflang supports JSON serialization only")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            JsonNull -> null
            is JsonPrimitive -> listOf(element.stringContent("hreflang"))
            is JsonArray -> element.map { it.stringContent("hreflang") }
            else -> throw SerializationException("JSON:API hreflang must be a string or an array of strings")
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>?) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("JSON:API hreflang supports JSON serialization only")
        jsonEncoder.encodeJsonElement(
            when {
                value == null -> JsonNull
                value.size == 1 -> JsonPrimitive(value.single())
                else -> JsonArray(value.map(::JsonPrimitive))
            },
        )
    }
}

private fun JsonElement.stringContent(name: String): String =
    (this as? JsonPrimitive)?.takeIf { it.isString }?.content
        ?: throw SerializationException("JSON:API $name must contain strings")
