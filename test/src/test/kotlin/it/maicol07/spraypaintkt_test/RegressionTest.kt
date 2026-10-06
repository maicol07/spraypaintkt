package it.maicol07.spraypaintkt_test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import it.maicol07.spraypaintkt.JsonApiResource
import it.maicol07.spraypaintkt.JsonApiSingleResponse
import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt.extensions.trackChanges
import it.maicol07.spraypaintkt.util.Deserializer
import it.maicol07.spraypaintkt.util.pluralize
import it.maicol07.spraypaintkt_ktor_integration.KtorHttpClient
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.BookGenre
import it.maicol07.spraypaintkt_test.models.NetworkResource
import it.maicol07.spraypaintkt_test.models.Publisher
import it.maicol07.spraypaintkt_test.models.RecordingHttpClient
import it.maicol07.spraypaintkt_test.models.RelationOptions
import it.maicol07.spraypaintkt_test.models.Review
import it.maicol07.spraypaintkt_test.models.StubResponse
import kotlinx.coroutines.Job
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class RegressionTest : FunSpec({
    test("constructor stores enum wire value") {
        Book(genre = BookGenre.FANTASY).genre shouldBe BookGenre.FANTASY
    }

    test("nullable enums support explicit null values") {
        val book = Book(genre = BookGenre.FANTASY)
        book.genre = null
        book.genre shouldBe null
        book.fromJsonApiResponse(
            JsonApiSingleResponse.fromJsonApiString(
                """{"data":{"type":"Book","id":"1","attributes":{"genre":null}}}"""
            )
        )
        book.genre shouldBe null
        book.attributes["genre"] = 1
        shouldThrow<ClassCastException> { book.genre }
    }

    test("default to-many relationships retain mutations and serialize dirty linkage") {
        val review = Review().apply { id = "review-1" }
        val book = Book()
        val reviews = book.reviews
        book.relationships.getChanges() shouldBe emptyMap()
        reviews.add(review)
        book.reviews shouldBe listOf(review)
        book.relationships.getChanges()["reviews"] shouldBe listOf(review)
        val linkage = Json.parseToJsonElement(book.toJsonApiString(onlyDirty = true))
            .jsonObject["data"]!!.jsonObject["relationships"]!!.jsonObject["reviews"]!!.jsonObject["data"]!!.jsonArray
        linkage.single().jsonObject["id"] shouldBe JsonPrimitive("review-1")
    }

    test("assigned and constructor to-many lists track subsequent mutations") {
        val review = Review().apply { id = "review-1" }
        for (book in listOf(
            Book(reviews = mutableListOf()),
            Book().apply { reviews = mutableListOf() },
        )) {
            book.relationships.clearChanges()
            book.reviews.add(review)
            book.relationships.getChanges()["reviews"] shouldBe listOf(review)
            book.relationships.clearChanges()
            book.reviews.clear()
            book.relationships.getChanges()["reviews"] shouldBe emptyList<Review>()
        }
        val readOnly = RelationOptions(books = listOf(Book().apply { id = "book-1" }))
        readOnly.books shouldHaveSize 1
    }

    test("nullable and schema-default to-many lists track mutations") {
        val book = Book().apply { id = "book-1" }
        val model = RelationOptions()
        model.optionalBooks shouldBe null
        model.optionalBooks = mutableListOf()
        model.relationships.clearChanges()
        model.optionalBooks!!.add(book)
        model.relationships.getChanges()["optional_books"] shouldBe listOf(book)
        model.optionalBooks = null
        model.optionalBooks shouldBe null
        model.relationships.getChanges()["optional_books"] shouldBe null
        model.relationships.clearChanges()
        model.defaultBooks.add(book)
        model.defaultBooks shouldBe listOf(book)
        model.relationships.getChanges()["default_books"] shouldBe listOf(book)
    }

    test("explicit null clears a to-one relationship while missing linkage preserves it") {
        val publisher = Publisher().apply { id = "publisher-1" }
        val book = Book(publisher = publisher)
        book.fromJsonApiResponse(
            JsonApiSingleResponse.fromJsonApiString(
                """{"data":{"type":"Book","id":"1","relationships":{"publisher":{"links":{"related":"/publishers"}}}}}"""
            )
        )
        book.publisher shouldBe publisher
        book.fromJsonApiResponse(
            JsonApiSingleResponse.fromJsonApiString(
                """{"data":{"type":"Book","id":"1","relationships":{"publisher":{"data":null}}}}"""
            )
        )
        book.relationships.containsKey("publisher") shouldBe true
        book.relationships["publisher"] shouldBe null
        book.relationships.getChanges() shouldBe emptyMap()
    }

    test("deserializing a null resource reports a serialization error without changing the target") {
        val response = JsonApiSingleResponse(data = null)
        shouldThrow<SerializationException> { Deserializer().deserialize(response) }
        val book = Book(title = "original").apply { id = "original-id" }
        shouldThrow<SerializationException> { book.fromJsonApiResponse(response) }
        book.id shouldBe "original-id"
        book.title shouldBe "original"
    }

    test("resource type mismatches are rejected before modifying models") {
        val book = Book(title = "original").apply { id = "original-id" }
        shouldThrow<SerializationException> {
            book.fromJsonApi(JsonApiResource(id = "other", type = "Publisher"))
        }
        book.id shouldBe "original-id"
        book.title shouldBe "original"

        RecordingHttpClient.response = StubResponse(200, """{"data":[{"type":"Book","id":"1"}]}""")
        shouldThrow<SerializationException> { NetworkResource.firstOrNull() }
        shouldThrow<SerializationException> { NetworkResource.all() }
        RecordingHttpClient.response = StubResponse(200, """{"data":{"type":"Book","id":"1"}}""")
        shouldThrow<SerializationException> { NetworkResource.findOrNull("1") }
    }

    test("closing a Ktor adapter preserves a caller-owned client") {
        val client = HttpClient(CIO)
        try {
            val adapter = KtorHttpClient(httpClient = client)
            adapter.close()
            adapter.close()
            client.coroutineContext[Job]!!.isActive shouldBe true
        } finally {
            client.close()
        }
        client.coroutineContext[Job]!!.isActive shouldBe false
    }

    test("closing a Ktor adapter closes its own client engine") {
        val engine = CIO.create()
        val adapter = KtorHttpClient(
            engineFactory = object : HttpClientEngineFactory<CIOEngineConfig> {
                override fun create(block: CIOEngineConfig.() -> Unit) = engine
            }
        )
        try {
            adapter.close()
            engine.coroutineContext[Job]!!.isActive shouldBe false
        } finally {
            engine.close()
        }
    }

    test("pluralization preserves irregular and suffix rules") {
        "person".pluralize() shouldBe "people"
        "status".pluralize() shouldBe "statuses"
    }

    test("to-many relationship remains an array with one item") {
        val review = Review().apply { id = "review-1" }
        val book = Book(reviews = mutableListOf(review))
        val relationship = Json.parseToJsonElement(book.toJsonApiString())
            .jsonObject["data"]!!.jsonObject["relationships"]!!.jsonObject["reviews"]!!.jsonObject

        relationship["data"]!!.shouldBeInstanceOf<kotlinx.serialization.json.JsonArray>()
            .shouldHaveSize(1)
    }

    test("relation annotation options are generated") {
        val model = RelationOptions(books = listOf(Book().apply { id = "book-1" }))
        val relationships = Json.parseToJsonElement(model.toJsonApiString())
            .jsonObject["data"]!!.jsonObject["relationships"]!!.jsonObject

        relationships.containsKey("published_books") shouldBe true
        relationships["published_books"]!!.jsonObject["data"]!!.jsonArray.shouldHaveSize(1)
    }

    test("collection views cannot bypass dirty tracking") {
        val list = mutableListOf("a", "b").trackChanges()
        list.subList(0, 1).clear()
        list.getChanges() shouldBe listOf("a")

        val map = mutableMapOf("a" to 1, "b" to 2).trackChanges()
        map.entries.first { it.key == "a" }.setValue(3)
        map.keys.remove("b")
        map.getChanges() shouldBe mapOf("a" to 3, "b" to null)
    }

    test("multiple sort fields use one JSON API parameter") {
        RecordingHttpClient.response = StubResponse(200, "{\"data\":[]}")
        NetworkResource.order("name").order("id", SortDirection.DESC).all()

        RecordingHttpClient.lastParameters["sort"] shouldBe "name,-id"
    }

    test("first and last queries handle empty and populated collections") {
        RecordingHttpClient.response = StubResponse(
            200,
            """{"links":{"self":"/api/library_entries"},"meta":{"totalItems":0},"data":[]}"""
        )
        val scope = NetworkResource.scope().limit(30).order("name")
        val empty = scope.firstOrNull()
        empty.data shouldBe null
        empty.meta["totalItems"] shouldBe JsonPrimitive(0)
        empty.raw.links?.self?.href shouldBe "/api/library_entries"
        RecordingHttpClient.lastParameters["page[limit]"] shouldBe "1"
        scope.pagination.limit shouldBe 30
        scope.exists() shouldBe false
        scope.lastOrNull().data shouldBe null
        scope.sort["name"] shouldBe SortDirection.ASC
        shouldThrow<NoSuchElementException> { scope.first() }
        shouldThrow<NoSuchElementException> { scope.last() }

        RecordingHttpClient.response = StubResponse(
            200,
            """
            {"data":[
                {"type":"network-resource","id":"1","attributes":{"name":"first"}},
                {"type":"network-resource","id":"2","attributes":{"name":"second"}}
            ]}
            """.trimIndent()
        )
        scope.firstOrNull().data?.name shouldBe "first"
        scope.first().data.id shouldBe "1"
        scope.lastOrNull().data?.id shouldBe "1"
        scope.last().data.id shouldBe "1"
        scope.exists() shouldBe true
        scope.pagination.limit shouldBe 30
        scope.sort["name"] shouldBe SortDirection.ASC
    }

    test("accepted writes succeed and clear dirty state") {
        RecordingHttpClient.response = StubResponse(202, "")
        val resource = NetworkResource(name = "before").apply {
            isPersisted = true
            attributes.clearChanges()
            name = "after"
        }

        resource.save()
        resource.attributes.getChanges() shouldBe emptyMap()
        resource.destroy()
    }

    test("resource IDs are encoded as URL path segments") {
        RecordingHttpClient.response = StubResponse(200, "{\"data\":null}")
        NetworkResource.findOrNull("a/b c")

        RecordingHttpClient.lastUrl shouldBe "https://example.invalid/network-resources/a%2Fb%20c"
    }
})
