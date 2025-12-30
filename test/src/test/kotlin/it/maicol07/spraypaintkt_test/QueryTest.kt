package it.maicol07.spraypaintkt_test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.ExtendedBook
import it.maicol07.spraypaintkt_test.models.Review

class QueryTest : FunSpec({
    test("all") {
        val reviews = Review.all()
        reviews.data.shouldBeInstanceOf<List<Review>>()

        val review = reviews.data.first()
        review.type shouldBe "Review"
    }

    test("find") {
        val response = Review.first()
        val firstReview = response.data
        val review = Review.find(firstReview.id!!)

        review.data.shouldBeInstanceOf<Review>()

        val resource = review.data
        resource.type shouldBe "Review"
        resource.id shouldBe firstReview.id
        resource.review shouldBe firstReview.review
    }

    test("first") {
        val review = Review.first()
        review.data.type shouldBe "Review"
    }

    test("filter") {
        val discussion = Review.where("review", "review 10").first()
        discussion.data.shouldBeInstanceOf<Review>()

        discussion.data.review shouldBe "review 10"
    }

    test("sort") {
        // We can't sort by created because they're all the same
        val reviews = Review.order("review", SortDirection.DESC).first()
        reviews.data.shouldBeInstanceOf<Review>()

        val review = reviews.data
        review.type shouldBe "Review"
        review.review shouldBe "review 99"
    }

    test("include") {
        val firstReview = Review.first().data
        val reviews = Review.includes("book", "reader", "book.publisher", "book.publisher.books").find(firstReview.id!!)
        reviews.data.shouldBeInstanceOf<Review>()

        val review = reviews.data
        review.type shouldBe "Review"
        review.id shouldBe firstReview.id

        val reader = review.reader
        reader.type shouldBe "Person"
        reader.id shouldBe review.readerId.toString()

        val book = review.book
        book.id shouldBe review.bookId

        val publisher = book.publisher
        publisher.type shouldBe "Publisher"
        publisher.id shouldBe book.publisherId.toString()

        val publishedBooks = review.book.publisher.books
        publishedBooks.shouldBeInstanceOf<List<Book>>()
        publishedBooks.shouldHaveSize(1)
        publishedBooks[0].type shouldBe "Book"
        publishedBooks[0].id shouldBe book.id
    }

    test("page") {
        val reviews = Review.offset(1).limit(3).all()
        reviews.data.shouldBeInstanceOf<List<Review>>()
        reviews.data.shouldHaveSize(3)

        val review = reviews.data.first()
        review.type shouldBe "Review"
    }

    test("extended") {
        val books = ExtendedBook.all()
        books.data.shouldBeInstanceOf<List<ExtendedBook>>()
        val book = books.data.first()
        book.shouldBeInstanceOf<ExtendedBook>()
        book.type shouldBe "Book"
    }

    test("findOrNull") {
        val response = Review.first()
        val firstReview = response.data
        val review = Review.findOrNull(firstReview.id!!)

        review.data.shouldBeInstanceOf<Review>()
        review.data!!.id shouldBe firstReview.id

        val notFound = Book.findOrNull("not-found")
        notFound.data shouldBe null
    }

    test("exists") {
        Review.exists() shouldBe true
        Review.where("review", "not-found").exists() shouldBe false
    }

    test("last") {
        val review = Review.last()
        review.data.shouldBeInstanceOf<Review>()
    }

    test("lastOrNull") {
        val review = Review.lastOrNull()
        review.data.shouldBeInstanceOf<Review>()
    }

    test("firstOrNull") {
        val review = Review.firstOrNull()
        review.data.shouldBeInstanceOf<Review>()

        val notFound = Review.where("review", "not-found").firstOrNull()
        notFound.data shouldBe null
    }

    test("extraParam") {
        val reviews = Review.extraParam("foo", "bar").first()
        reviews.data.shouldBeInstanceOf<Review>()
    }

    test("select") {
        val review = Review.select("reviews", "review").first().data
        review.review.shouldNotBeNull()
    }

    test("page based pagination throws") {
        shouldThrow<RuntimeException> {
            Review.page(1)
        }.message shouldBe "Page-based pagination is not supported with the current pagination strategy"

        shouldThrow<RuntimeException> {
            Review.per(10)
        }.message shouldBe "Page-based pagination is not supported with the current pagination strategy"
    }
})
