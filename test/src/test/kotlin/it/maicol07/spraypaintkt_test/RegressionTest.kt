package it.maicol07.spraypaintkt_test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt.extensions.trackChanges
import it.maicol07.spraypaintkt.util.pluralize
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.BookGenre
import it.maicol07.spraypaintkt_test.models.NetworkResource
import it.maicol07.spraypaintkt_test.models.RecordingHttpClient
import it.maicol07.spraypaintkt_test.models.RelationOptions
import it.maicol07.spraypaintkt_test.models.Review
import it.maicol07.spraypaintkt_test.models.StubResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class RegressionTest : FunSpec({
    test("constructor stores enum wire value") {
        Book(genre = BookGenre.FANTASY).genre shouldBe BookGenre.FANTASY
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
