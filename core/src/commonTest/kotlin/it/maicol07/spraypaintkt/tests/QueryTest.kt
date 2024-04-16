package it.maicol07.spraypaintkt.tests

import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt.tests.models.Book
import it.maicol07.spraypaintkt.tests.models.Review
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class QueryTest : BaseTest() {
    @Test
    fun all() = runTest {
        val reviews = client.all<Review>()
        assertIs<List<Review>>(reviews.data)

        val discussion = reviews.data.first()
        assertEquals("Review", discussion.type)
    }

    @Test
    fun find() = runTest {
        val response = client.first<Review>()
        val firstReview = response.data
        val review = client.find<Review>(firstReview.id!!)

        assertIs<Review>(review.data)

        val resource = review.data
        assertEquals("Review", resource.type)
        assertEquals(firstReview.id, resource.id)
        assertEquals(firstReview.review, resource.review)
    }

    @Test
    fun first() = runTest {
        val review = client.first<Review>()
        assertEquals("Review", review.data.type)
    }

    @Test
    fun filter() = runTest {
        val discussion = client.where("review", "review 10").first<Review>()
        assertIs<Review>(discussion.data)

        assertEquals("review 10", discussion.data.review)
    }

    @Test
    fun sort() = runTest {
        // We can't sort by created because they're all the same
        val reviews = client.order("review", SortDirection.DESC).first<Review>()
        assertIs<Review>(reviews.data)

        val review = reviews.data
        assertEquals("Review", review.type)
        assertEquals("review 99", review.review)
    }

    @Test
    fun include() = runTest {
        val firstReview = client.first<Review>().data
        val reviews = client.includes("book", "reader", "book.publisher", "book.publisher.books").find<Review>(firstReview.id!!)
        assertIs<Review>(reviews.data)

        val review = reviews.data
        assertEquals("Review", review.type)
        assertEquals(firstReview.id, review.id)

        val reader = review.reader
        assertEquals("Person", reader.type)
        assertEquals(review.reader_id.toString(), reader.id)

        val book = review.book
        assertEquals(review.book_id, book.id)

        val publisher = book.publisher
        assertEquals("Publisher", publisher.type)
        assertEquals(book.publisher_id.toString(), publisher.id)

        val publishedBooks = review.book.publisher.books
        assertIs<List<Book>>(publishedBooks)
        assertEquals(1, publishedBooks.size)
        assertEquals("Book", publishedBooks[0].type)
        assertEquals(book.id, publishedBooks[0].id)
    }

    @Test
    fun page() = runTest {
        val reviews = client.offset(1).limit(3).all<Review>()
        assertIs<List<Review>>(reviews.data)
        assertEquals(3, reviews.data.size)

        val review = reviews.data.first()
        assertEquals("Review", review.type)
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