package it.maicol07.spraypaintkt.interfaces

import it.maicol07.spraypaintkt.PaginationStrategy

/**
 * Interface for the configuration object of JSON:API resources.
 */
interface JsonApiConfig {
    /** The base URL of the API. */
    val baseUrl: String

    /** The namespace of the API. */
    val apiNamespace: String
        get() = ""

    /** The pagination strategy of the API. */
    val paginationStrategy: PaginationStrategy
        get() = PaginationStrategy.PAGE_BASED

    /** The HTTP client to use for requests. */
    val httpClient: HttpClient
}