package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ResourceSerializer: KSerializer<Resource> {
    override val descriptor = PrimitiveSerialDescriptor("Resource", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Resource) {
        encoder.encodeString(value.toJsonApiString())
    }

    override fun deserialize(decoder: Decoder): Resource {
        val jsonApiString = decoder.decodeString().trim('"')
        val response = JsonApiSingleResponse.fromJsonApiString(jsonApiString)
        if (response.data == null) {
            val resource = JsonApiResource.fromJsonApiString(jsonApiString)
            return Deserializer().deserialize(resource)
        }

        return Deserializer().deserialize(response)
    }
}