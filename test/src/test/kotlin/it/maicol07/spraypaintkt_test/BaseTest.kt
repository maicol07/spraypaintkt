package it.maicol07.spraypaintkt_test

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import it.maicol07.spraypaintkt.PaginationStrategy
import it.maicol07.spraypaintkt.interfaces.HttpClient
import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import it.maicol07.spraypaintkt_annotation.DefaultInstance
import it.maicol07.spraypaintkt_ktor_integration.KtorHttpClient

abstract class BaseTest {
    @DefaultInstance
    data object AppJsonApiConfig: JsonApiConfig {
        override val baseUrl: String = "https://maicol07.eu.pythonanywhere.com/api"
        override val paginationStrategy: PaginationStrategy = PaginationStrategy.OFFSET_BASED
        override val httpClient: HttpClient = KtorHttpClient(httpClientOptions = {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000 // 1 minute
            }
        })
    }
}
