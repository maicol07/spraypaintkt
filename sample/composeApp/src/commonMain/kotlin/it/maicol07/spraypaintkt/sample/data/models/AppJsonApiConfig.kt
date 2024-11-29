package it.maicol07.spraypaintkt.sample.data.models

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import it.maicol07.spraypaintkt.PaginationStrategy
import it.maicol07.spraypaintkt.interfaces.HttpClient
import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import it.maicol07.spraypaintkt_annotation.DefaultInstance
import it.maicol07.spraypaintkt_ktor_integration.KtorHttpClient

@DefaultInstance
data object AppJsonApiConfig: JsonApiConfig {
    override val baseUrl: String = "https://safrs.onrender.com/api"
    override val paginationStrategy: PaginationStrategy = PaginationStrategy.OFFSET_BASED
    override val httpClient: HttpClient = KtorHttpClient(httpClientOptions = {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 90000
        }
    })
}