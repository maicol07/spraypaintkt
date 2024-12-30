package it.maicol07.spraypaintkt_test

import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt.extensions.destroy
import it.maicol07.spraypaintkt.extensions.save
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.Person
import it.maicol07.spraypaintkt_test.models.Review
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WritingTests : BaseTest() {
    @Test(timeout = 100_000)
    fun writing() = runTest {
        val lastPerson = Person.order("id", SortDirection.DESC).first().data
        var person = Person()
        person.name = "John Doe"
        person.email = "john@doe.com"
        person.comment = "This is a comment"
        person.dob = "1990-01-01"
        assertTrue { person.save() }
        // Assert new ID is greater than the last one
        assertTrue { person.id!! > lastPerson.id!! } // For some reason multiple requests are made and the ID is not the last one + 1

        // Refresh the person object
        person = Person.find(person.id!!).data

        person.name = "Jane Doe"
        assertTrue { person.save() }

        val updatedResponse = Person.find(person.id!!)
        val updatedPerson = updatedResponse.data
        assertEquals("Jane Doe", updatedPerson.name)
        person = updatedPerson

        // Refresh the person object
        person = Person.find(person.id!!).data

        // Add relationship
        val book = Book.first().data
        var review = Review()
        review.review = "This is a review"
        review.book = book
        review.reader = person
        assertTrue { review.save() }

        // Refresh the review object
        review = Review.includes("book", "reader").find(review.id!!).data
        // Currently bugged
//        assertEquals(person.id, review.reader.id)
//        assertEquals(book.id, review.book.id)

        // Disabled due to DEMO server bug
//        println("Destroying review")
//        assertTrue {
//            try {
//                review.destroy()
//            } catch (e: JsonApiException) {
//                println("Status code: ${e.statusCode}")
//                println("Body: ${e.body}")
//                false
//            }
//        }
        println("Destroying person")
        assertTrue {
            try {
                person.destroy()
            } catch (e: JsonApiException) {
                println("Status code: ${e.statusCode}")
                println("Body: ${e.body}")
                false
            }
        }

        try {
            Review.find(review.id!!)
        } catch (e: JsonApiException) {
            assertTrue { e.statusCode == 404 }
        }
        try {
            Person.find(person.id!!)
        } catch (e: JsonApiException) {
            assertTrue { e.statusCode == 404 }
        }
    }
}