package it.maicol07.spraypaintkt.interfaces

import it.maicol07.spraypaintkt.PaginationStrategy

interface JsonApiConfig {
    val baseUrl: String
    val apiNamespace: String
        get() = ""
    val paginationStrategy: PaginationStrategy
        get() = PaginationStrategy.PAGE_BASED
    val httpClient: HttpClient
}