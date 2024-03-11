package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.util.Deserializer
import it.maicol07.spraypaintkt.http.HttpClient

@Suppress("unused")
class Client(
    baseUrl: String,
    apiNamespace: String = "",
    val paginationStrategy: PaginationStrategy = PaginationStrategy.PAGE_BASED,
    val httpClient: HttpClient,
    val modelGenerator: ModelGenerator
) {
    val typeRegistry = mutableMapOf<String, () -> Resource>()
    val deserializer = Deserializer(typeRegistry)
    private val fullBasePath = "$baseUrl$apiNamespace"

    fun scope(): Scope {
        return Scope(this)
    }

    /**
     * Set the number of items per page (Only for page-based pagination)
     */
    fun per(pageSize: Number): Scope {
        return scope().per(pageSize)
    }


    /**
     * Set the page number to return (Only for page-based pagination)
     */
    fun page(pageNumber: Number): Scope {
        return scope().page(pageNumber)
    }

    /**
     * Set the number of items to return (Only for offset-based pagination)
     */
    fun limit(limit: Number): Scope {
        return scope().limit(limit)
    }

    /**
     * Set the number of items to skip (Only for offset-based pagination)
     */
    fun offset(offset: Number): Scope {
        return scope().offset(offset)
    }

    fun where(key: String, value: String): Scope {
        return scope().where(key, value)
    }

    fun order(key: String, direction: SortDirection): Scope {
        return scope().order(key, direction)
    }

    fun includes(vararg relationships: String): Scope {
        return scope().includes(*relationships)
    }

    fun select(type: String, vararg fields: String): Scope {
        return scope().select(type, *fields)
    }

    suspend inline fun <reified R: Resource> all(): CollectionProxy<R> {
        return scope().all()
    }

    suspend inline fun <reified R: Resource> find(id: String): RecordProxy<R> {
        return scope().find<R>(id)
    }

    suspend inline fun <reified R: Resource> first(): RecordProxy<R> {
        return scope().first<R>()
    }

    inline fun <reified R: Resource> registerResource(type: String? = null): Client {
        val resource = modelGenerator.generate(R::class)
        typeRegistry[type ?: resource.type] = { modelGenerator.generate(R::class) }
        return this
    }

    fun <R: Resource> urlForResource(resource: R, id: String? = null): String {
        val endpoint = resource.endpoint ?: resource.type
        val effectiveId = id ?: resource.id
        return "$fullBasePath/$endpoint${effectiveId?.let { "/$it" } ?: ""}"
    }
}