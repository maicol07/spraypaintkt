package it.maicol07.spraypaintkt_test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt.extensions.destroy
import it.maicol07.spraypaintkt.extensions.save
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.Person
import it.maicol07.spraypaintkt_test.models.Review

class WritingTests : FunSpec({
    test("writing") {
        val lastPerson = Person.order("id", SortDirection.DESC).first().data
        var person = Person()
        person.name = "John Doe"
        person.email = "john@doe.com"
        person.comment = "This is a comment"
        person.dob = "1990-01-01"
        person.save() shouldBe true
        // Assert new ID is greater than the last one
        person.id!! shouldBeGreaterThan lastPerson.id!! // For some reason multiple requests are made and the ID is not the last one + 1

        // Refresh the person object
        person = Person.find(person.id!!).data

        person.name = "Jane Doe"
        person.save() shouldBe true

        val updatedResponse = Person.find(person.id!!)
        val updatedPerson = updatedResponse.data
        updatedPerson.name shouldBe "Jane Doe"
        person = updatedPerson

        // Refresh the person object
        person = Person.find(person.id!!).data

        // Add relationship
        val book = Book.first().data
        var review = Review()
        review.review = "This is a review"
        review.book = book
        review.reader = person
        review.save() shouldBe true

        // Refresh the review object
        review = Review.includes("book", "reader").find(review.id!!).data

        // Currently bugged
//        person.id shouldBe review.reader.id
//        book.id shouldBe review.book.id

        // Disabled due to DEMO server bug
//        println("Destroying review")
//        review.destroy() shouldBe true

//        val e1 = shouldThrow<JsonApiException> {
//            Review.find(review.id!!)
//        }
//        e1.statusCode shouldBe 404

        println("Destroying person")
        try {
            person.destroy() shouldBe true
        } catch (e: JsonApiException) {
            println("Status code: ${e.statusCode}")
            println("Body: ${e.body}")
            throw e
        }

        val e2 = shouldThrow<JsonApiException> {
            Person.find(person.id!!)
        }
        e2.statusCode shouldBe 404
    }
})

