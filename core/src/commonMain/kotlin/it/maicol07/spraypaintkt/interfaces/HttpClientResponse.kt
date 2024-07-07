package it.maicol07.spraypaintkt.interfaces

/**
 * Interface for a generic HTTP client response.
 */
interface HttpClientResponse {
    /**
     * The status code of the response.
     */
    val statusCode: Int

    /**
     * The body of the response.
     */
    val body: String
}