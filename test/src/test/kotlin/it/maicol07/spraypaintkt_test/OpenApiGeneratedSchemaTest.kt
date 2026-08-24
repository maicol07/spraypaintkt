package it.maicol07.spraypaintkt_test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.maicol07.spraypaintkt.JsonApiSingleResponse
import it.maicol07.spraypaintkt_test.models.apiplatform.LibraryEntry
import it.maicol07.spraypaintkt_test.models.apiplatform.LibraryEntryCompletionStatus
import it.maicol07.spraypaintkt_test.models.apiplatform.LibraryEntryStatus
import it.maicol07.spraypaintkt_test.models.openapi.OpenApiBook
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * The models under `models/openapi` and `models/apiplatform` are the verbatim output of
 * `:openapi-generator:generateSchemas` run against the two fixtures in
 * `openapi-generator/src/test/resources`. They guard the contract between the generator's type
 * mapping and what the deserializer actually stores in `attributes`.
 */
class OpenApiGeneratedSchemaTest : FunSpec({
    test("generated schemas deserialize the shapes their OpenAPI types promise") {
        val book = OpenApiBook()
        book.fromJsonApiResponse(
            JsonApiSingleResponse.fromJsonApiString(
                """
                {"data":{"type":"OpenApiBook","id":"1","attributes":{
                    "title":"Dune","page_count":412,"publisher_id":7,"rating":4.5,"published":true,
                    "tags":["classic","scifi"],"metadata":{"isbn":"9780441013593"}
                },"relationships":{
                    "author":{"data":{"type":"OpenApiPerson","id":"2"}},
                    "reviews":{"data":[{"type":"OpenApiReview","id":"3"}]}
                }},"included":[
                    {"type":"OpenApiPerson","id":"2","attributes":{"name":"Frank Herbert"}},
                    {"type":"OpenApiReview","id":"3","attributes":{"body":"Great"}}
                ]}
                """.trimIndent(),
            ),
        )

        book.title shouldBe "Dune"
        book.pageCount shouldBe 412
        book.publisherId shouldBe 7
        book.rating shouldBe 4.5f
        book.published shouldBe true
        book.tags shouldBe listOf("classic", "scifi")
        book.metadata shouldBe mapOf("isbn" to "9780441013593")
        book.author.name shouldBe "Frank Herbert"
        book.reader shouldBe null
        book.reviews shouldHaveSize 1
    }

    test("generated schemas keep the endpoint and type declared by the document") {
        OpenApiBook.resourceType shouldBe "OpenApiBook"
        OpenApiBook.endpoint shouldBe "openapi-books"
    }

    test("API Platform output round-trips enums, read-only members and a nullable linkage") {
        val entry = LibraryEntry()
        entry.fromJsonApiResponse(
            JsonApiSingleResponse.fromJsonApiString(
                """
                {"data":{"type":"LibraryEntry","id":"1","attributes":{
                    "game_id":42,"status":"PLAYING","completion_status":"MAIN_STORY","owned":true,
                    "created_at":"2026-01-01T00:00:00+00:00","platforms_ids":[1,2]
                },"relationships":{"user":{"data":{"type":"User","id":"9"}}}},
                "included":[{"type":"User","id":"9","attributes":{"nickname":"maicol"}}]}
                """.trimIndent(),
            ),
        )

        entry.gameId shouldBe 42
        entry.status shouldBe LibraryEntryStatus.PLAYING
        entry.completionStatus shouldBe LibraryEntryCompletionStatus.MAIN_STORY
        entry.owned shouldBe true
        entry.createdAt shouldBe "2026-01-01T00:00:00+00:00"
        entry.platformsIds shouldBe listOf(1, 2)
        entry.user?.nickname shouldBe "maicol"

        entry.status = LibraryEntryStatus.COMPLETED
        Json.parseToJsonElement(entry.toJsonApiString())
            .jsonObject["data"]!!.jsonObject["attributes"]!!.jsonObject["status"]!!
            .jsonPrimitive.content shouldBe "COMPLETED"
    }
})
