package it.maicol07.spraypaintkt.interfaces

/**
 * Interface for a generic HTTP client.
 */
interface HttpClient {
    /**
     * Sends a GET request to the specified URL.
     */
    suspend fun get(url: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse

    /**
     * Sends a PATCH request to the specified URL.
     */
    suspend fun patch(url: String, body: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse

    /**
     * Sends a POST request to the specified URL.
     */
    suspend fun post(url: String, body: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse

    /**
     * Sends a PUT request to the specified URL.
     */
    suspend fun put(url: String, body: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse

    /**
     * Sends a DELETE request to the specified URL.
     */
    suspend fun delete(url: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse
}