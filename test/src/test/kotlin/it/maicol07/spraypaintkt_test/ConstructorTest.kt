package it.maicol07.spraypaintkt_test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.Person
import it.maicol07.spraypaintkt_test.models.Review

class ConstructorTest : FunSpec({
    test("Constructor") {
        val person = Person(
            name = "John Doe",
            email = "john@doe.com",
            comment = "A comment",
            dob = "1990-01-01"
        )

        person.name shouldBe "John Doe"
        person.email shouldBe "john@doe.com"
        person.comment shouldBe "A comment"
        person.dob shouldBe "1990-01-01"
    }

    test("Constructor with relations") {
        val person = Person(name = "Reader")

        val book = Book(title = "Book Title")

        val review = Review(
            review = "Great book",
            book = book,
            reader = person
        )

        review.review shouldBe "Great book"
        review.book shouldBe book
        review.reader shouldBe person
    }

    test("Partial constructor") {
        val person = Person(name = "John Doe")
        person.name shouldBe "John Doe"

        shouldThrow<NoSuchElementException> {
            person.email
        }
    }
})

