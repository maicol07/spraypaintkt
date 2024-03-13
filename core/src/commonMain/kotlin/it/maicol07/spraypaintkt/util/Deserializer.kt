package it.maicol07.spraypaintkt.util

import it.maicol07.spraypaintkt.JsonApiResource
import it.maicol07.spraypaintkt.Resource

class Deserializer(private val typeRegistry: Map<String, () -> Resource>) {
    private val cache = mutableMapOf<Pair<String, String>, Resource>()
    fun <R: Resource> deserialize(model: R, datum: JsonApiResource, included: List<JsonApiResource>): R {
        // already

//        Logger.d("Deserializer") { "Deserializing ${model.type} with id ${datum.id}" }
        model.id = datum.id
//        model.tempId = datum["temp-id"]?.extractedContent.toString()

        model.attributes.putAll(datum.attributes)

        for ((key, relationship) in datum.relationships) {
            val relationData = relationship?.data
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

                if (relatedResources.size == 1 && relationship.isSingle) {
                    model.relationships[key] = relatedResources.first()
                } else {
                    model.relationships[key] = relatedResources
                }
            }
        }

        model.links.putAll(datum.links)
        model.meta.putAll(datum.meta)

        return model
    }
}