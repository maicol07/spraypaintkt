package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class ResourceSerializer<R: Resource>: KSerializer<R> {
    override val descriptor = PrimitiveSerialDescriptor("Resource", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: R) {
        encoder.encodeString(value.toJsonApiString())
    }

    override fun deserialize(decoder: Decoder): R {
        val jsonApiString = decoder.decodeString().trim('"')

        val response = JsonApiSingleResponse.fromJsonApiString(jsonApiString)
        val resource = if (response.data == null) {
            val response = JsonApiResource.fromJsonApiString(jsonApiString)
            Deserializer().deserialize(response)
        } else {
            Deserializer().deserialize(response)
        }

        @Suppress("UNCHECKED_CAST") // We know it's a resource
        return resource as? R ?: throw IllegalArgumentException("Resource is not of type R")
    }
}