package it.maicol07.spraypaintkt_test

import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.ExtendedBook
import it.maicol07.spraypaintkt_test.models.Review
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class QueryTest : BaseTest() {
    @Test
    fun all() = runTest {
        val reviews = Review.all()
        assertIs<List<Review>>(reviews.data)

        val review = reviews.data.first()
        assertEquals("Review", review.type)
    }

    @Test
    fun find() = runTest {
        val response = Review.first()
        val firstReview = response.data
        val review = Review.find(firstReview.id!!)

        assertIs<Review>(review.data)

        val resource = review.data
        assertEquals("Review", resource.type)
        assertEquals(firstReview.id, resource.id)
        assertEquals(firstReview.review, resource.review)
    }

    @Test
    fun first() = runTest {
        val review = Review.first()
        assertEquals("Review", review.data.type)
    }

    @Test
    fun filter() = runTest {
        val discussion = Review.where("review", "review 10").first()
        assertIs<Review>(discussion.data)

        assertEquals("review 10", discussion.data.review)
    }

    @Test
    fun sort() = runTest {
        // We can't sort by created because they're all the same
        val reviews = Review.order("review", SortDirection.DESC).first()
        assertIs<Review>(reviews.data)

        val review = reviews.data
        assertEquals("Review", review.type)
        assertEquals("review 99", review.review)
    }

    @Test
    fun include() = runTest {
        val firstReview = Review.first().data
        val reviews = Review.includes("book", "reader", "book.publisher", "book.publisher.books").find(firstReview.id!!)
        assertIs<Review>(reviews.data)

        val review = reviews.data
        assertEquals("Review", review.type)
        assertEquals(firstReview.id, review.id)

        val reader = review.reader
        assertEquals("Person", reader.type)
        assertEquals(review.readerId.toString(), reader.id)

        val book = review.book
        assertEquals(review.bookId, book.id)

        val publisher = book.publisher
        assertEquals("Publisher", publisher.type)
        assertEquals(book.publisherId.toString(), publisher.id)

        val publishedBooks = review.book.publisher.books
        assertIs<List<Book>>(publishedBooks)
        assertEquals(1, publishedBooks.size)
        assertEquals("Book", publishedBooks[0].type)
        assertEquals(book.id, publishedBooks[0].id)
    }

    @Test
    fun page() = runTest {
        val reviews = Review.offset(1).limit(3).all()
        assertIs<List<Review>>(reviews.data)
        assertEquals(3, reviews.data.size)

        val review = reviews.data.first()
        assertEquals("Review", review.type)
    }

    @Test
    fun extended() = runTest {
        val books = ExtendedBook.all()
        assertIs<List<ExtendedBook>>(books.data)
        val book = books.data.first()
        assertIs<ExtendedBook>(book)
        assertEquals("Book", book.type)
    }

//    @Test
//    fun fields() = runTest {
//        val discussions = client.select("discussions", "title", "slug").first<Review>()
//
//        val discussion = discussions.data
//        assertEquals("discussions", discussion.type)
//        assertNotNull(discussion.title)
//        assertNotNull(discussion.slug)
////        assertNull(discussion.commentCount)
//        assertFails { discussion.commentCount }
//    }
}