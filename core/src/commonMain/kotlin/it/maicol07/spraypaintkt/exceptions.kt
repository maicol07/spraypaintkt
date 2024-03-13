package it.maicol07.spraypaintkt

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class JsonApiException(
    val statusCode: Int,
    val body: String
): RuntimeException() {
    val errors = Json.decodeFromString<JsonApiErrorResponse>(body).errors
}

@Serializable
data class JsonApiErrorResponse(
    val errors: List<JsonApiError>
)

@Serializable
data class JsonApiError(
    val id: String? = null,
    val links: Links? = null,
    val status: Int? = null,
    val code: String? = null,
    val title: String? = null,
    val detail: String? = null,
    val source: Source? = null,
    val meta: Map<String, @Contextual Any?> = emptyMap()
) {
    @Serializable
    data class Source(
        val pointer: String? = null,
        val parameter: String? = null
    )

    @Serializable
    data class Links(
        val about: String,
        val type: String? = null
    )
}