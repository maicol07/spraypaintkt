package eu.prepsoil.app.utils.jsonapiclient.util

import eu.prepsoil.app.utils.jsonapiclient.JsonApiResource
import eu.prepsoil.app.utils.jsonapiclient.Resource

class Deserializer(private val typeRegistry: Map<String, () -> Resource>) {
    private val cache = mutableMapOf<Pair<String, String>, Resource>()
    fun <R: Resource> deserialize(model: R, datum: JsonApiResource, included: List<JsonApiResource>): R {
        // already

//        Logger.d("Deserializer") { "Deserializing ${model.type} with id ${datum.id}" }
        model.id = datum.id
//        model.tempId = datum["temp-id"]?.extractedContent.toString()

        model.attributes.putAll(datum.attributes)

        for ((key, value) in datum.relationships) {
            val relationData = value?.data
            if (relationData != null) {
                val relatedResources = mutableListOf<Resource>()

                for (relationshipData in relationData) {
                    val type = relationshipData.type
                    val id = relationshipData.id
                    val related = included.find { it.type == type && it.id == id }
                    if (related != null) {
                        val cached = cache.getOrPut(Pair(type, id)) {
                            val resource = typeRegistry[type]?.invoke() ?: throw RuntimeException("No type registered for $type")
                            deserialize(resource, related, included)
                            resource
                        }
                        relatedResources.add(cached)
                    }
                }

                if (relatedResources.size == 1) {
                    model.relationships[key] = relatedResources.first()
                } else {
                    model.relationships[key] = relatedResources
                }
            }
        }

        return model
    }
}