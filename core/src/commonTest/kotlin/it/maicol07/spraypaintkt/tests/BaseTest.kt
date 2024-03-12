package it.maicol07.spraypaintkt.tests

import io.ktor.client.plugins.logging.Logging
import it.maicol07.spraypaintkt.Client
import it.maicol07.spraypaintkt.ModelGenerator
import it.maicol07.spraypaintkt.PaginationStrategy
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.tests.models.Discussion
import it.maicol07.spraypaintkt.tests.models.Group
import it.maicol07.spraypaintkt.tests.models.Post
import it.maicol07.spraypaintkt.tests.models.Tag
import it.maicol07.spraypaintkt.tests.models.User
import it.maicol07.spraypaintkt_ktor_integration.KtorHttpClient
import kotlin.reflect.KClass
import kotlin.test.BeforeTest

abstract class BaseTest {
    val client = Client(
        baseUrl = "https://discuss.flarum.org/api",
        modelGenerator = object : ModelGenerator {
            override fun <R : Resource> generate(clazz: KClass<R>): R {
                return when (clazz) {
                    Discussion::class -> Discussion()
                    Tag::class -> Tag()
                    User::class -> User()
                    Post::class -> Post()
                    Group::class -> Group()
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
        client.registerResource<Discussion>()
        client.registerResource<Tag>()
        client.registerResource<User>()
        client.registerResource<Post>()
        client.registerResource<Group>()
    }
}