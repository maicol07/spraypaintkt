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
import kotlin.test.BeforeTest

abstract class BaseTest {
    @DefaultInstance
    data object AppJsonApiConfig: JsonApiConfig {
        override val baseUrl: String = "https://safrs.onrender.com/api"
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

    @BeforeTest
    fun setup() {
        println("WARNING: The first request may take a bit, as the server is hosted on Render and it needs to wake up.")
    }
}