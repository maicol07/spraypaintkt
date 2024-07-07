package it.maicol07.spraypaintkt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

private val json = Json { ignoreUnknownKeys = true }

/**
 * An exception thrown by the JSON:API server.
 *
 * @param statusCode The status code of the response.
 * @param body The body of the response.
 */
@Serializable
class JsonApiException(
    val statusCode: Int,
    val body: String
): RuntimeException() {
    /**
     * The errors returned by the server.
     */
    val errors by lazy {
        try {
            json.decodeFromString<JsonApiErrorResponse>(body).errors
        } catch (e: Exception) {
            emptyList()
        }
    }
}

/**
 * An error returned by the JSON:API server.
 */
@Serializable
data class JsonApiErrorResponse(
    val errors: List<JsonApiError>
)

/**
 * An error returned by the JSON:API server.
 *
 * @param id A unique identifier for this particular occurrence of the problem.
 * @param links A links object. @see [JsonApiError.Links]
 * @param status The HTTP status code applicable to this problem, expressed as a string value.
 * @param code An application-specific error code, expressed as a string value.
 * @param title A short, human-readable summary of the problem.
 * @param detail A human-readable explanation specific to this occurrence of the problem.
 * @param source An object containing references to the source of the error.
 * @param meta A meta object containing non-standard meta-information about the error. @see [JsonApiError.meta]
 */
@Serializable
data class JsonApiError(
    val id: String? = null,
    val links: Links? = null,
    val status: String? = null,
    val code: String? = null,
    val title: String? = null,
    val detail: String? = null,
    val source: Source? = null,
    val meta: JsonObject? = null
) {
    /**
     * An object containing references to the source of the error.
     *
     * @param pointer A JSON Pointer [RFC6901] to the associated entity in the request document [e.g. "/data" for a primary data object, or "/data/attributes/title" for a specific attribute].
     * @param parameter A string indicating which URI query parameter caused the error.
     */
    @Serializable
    data class Source(
        val pointer: String? = null,
        val parameter: String? = null
    )

    /**
     * A links object containing the following members:
     *
     * @param about A link that leads to further details about this particular occurrence of the problem.
     * @param type A link that leads to further details about this particular occurrence of the problem.
     */
    @Serializable
    data class Links(
        val about: String? = null,
        val type: String? = null
    )
}