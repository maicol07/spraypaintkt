package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.util.Deserializer
import it.maicol07.spraypaintkt.extensions.JsonObjectMap
import it.maicol07.spraypaintkt.extensions.extractedContent
import it.maicol07.spraypaintkt.extensions.toJsonElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

abstract class ResourceSerializer<R: Resource>: KSerializer<R> {
    override val descriptor = buildClassSerialDescriptor("Resource")

    override fun serialize(encoder: Encoder, value: R) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("Resource supports JSON serialization only")
        jsonEncoder.encodeJsonElement(value.toJsonApi().toJsonElement())
    }

    override fun deserialize(decoder: Decoder): R {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("Resource supports JSON deserialization only")
        val element = jsonDecoder.decodeJsonElement().let {
            if (it is JsonPrimitive && it.isString) Json.parseToJsonElement(it.content) else it
        }
        @Suppress("UNCHECKED_CAST")
        val responseMap = element.extractedContent as? JsonObjectMap
            ?: throw SerializationException("Resource must be a JSON object")

        val response = JsonApiSingleResponse(responseMap)
        val resource = if (response.data == null) {
            Deserializer().deserialize(JsonApiResource(responseMap))
        } else {
            Deserializer().deserialize(response)
        }

        @Suppress("UNCHECKED_CAST") // We know it's a resource
        return resource as? R ?: throw SerializationException("Resource has an unexpected type")
    }
}
