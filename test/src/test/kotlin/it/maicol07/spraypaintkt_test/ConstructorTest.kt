package it.maicol07.spraypaintkt_test

import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.Person
import it.maicol07.spraypaintkt_test.models.Review
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstructorTest {
    @Test
    fun testConstructor() {
        val person = Person(
            name = "John Doe",
            email = "john@doe.com",
            comment = "A comment",
            dob = "1990-01-01"
        )

        assertEquals("John Doe", person.name)
        assertEquals("john@doe.com", person.email)
        assertEquals("A comment", person.comment)
        assertEquals("1990-01-01", person.dob)
    }

    @Test
    fun testConstructorWithRelationships() {
        val person = Person(name = "Reader")

        val book = Book(title = "Book Title")

        val review = Review(
            review = "Great book",
            book = book,
            reader = person
        )

        assertEquals("Great book", review.review)
        assertEquals(book, review.book)
        assertEquals(person, review.reader)
    }

    @Test
    fun testPartialConstructor() {
        val person = Person(name = "John Doe")
        assertEquals("John Doe", person.name)

        var exceptionThrown = false
        try {
            person.email
        } catch (e: NoSuchElementException) {
            exceptionThrown = true
        }
        assertEquals(true, exceptionThrown)
    }
}

