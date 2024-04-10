package it.maicol07.spraypaintkt.tests

import io.ktor.client.plugins.logging.Logging
import it.maicol07.spraypaintkt.Client
import it.maicol07.spraypaintkt.ModelGenerator
import it.maicol07.spraypaintkt.PaginationStrategy
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.tests.models.Review
import it.maicol07.spraypaintkt.tests.models.Book
import it.maicol07.spraypaintkt.tests.models.Publisher
import it.maicol07.spraypaintkt.tests.models.Person
import it.maicol07.spraypaintkt_ktor_integration.KtorHttpClient
import kotlin.reflect.KClass
import kotlin.test.BeforeTest

abstract class BaseTest {
    @Suppress("UNCHECKED_CAST")
    val client = Client(
        baseUrl = "https://thomaxxl.pythonanywhere.com/api",
        modelGenerator = object : ModelGenerator {
            override fun <R : Resource> generate(clazz: KClass<R>): R {
                return when (clazz) {
                    Review::class -> Review()
                    Person::class -> Person()
                    Publisher::class -> Publisher()
                    Book::class -> Book()
                    else -> throw IllegalArgumentException("Unknown resource class: $clazz")
                } as R
            }
        },
        paginationStrategy = PaginationStrategy.OFFSET_BASED,
        httpClient = KtorHttpClient({
            install(Logging)
        }),
    )

    @BeforeTest
    fun setup() {
        client.registerResource<Review>()
        client.registerResource<Person>()
        client.registerResource<Publisher>()
        client.registerResource<Book>()
    }
}