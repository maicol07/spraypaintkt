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
        val review = client.find<Review>("5056b91f-47f7-4e2e-a5bd-352c4445aff8_1")

        assertIs<Review>(review.data)

        val resource = review.data
        assertEquals("Review", resource.type)
        assertEquals("5056b91f-47f7-4e2e-a5bd-352c4445aff8_1", resource.id)
        assertEquals("review 0", review.data.review)
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
        assertEquals("0ff9069c-d15f-4049-a228-68a3652fbbcf_21", discussion.data.id)
    }

    @Test
    fun sort() = runTest {
        // We can't sort by created because they're all the same
        val reviews = client.order("book_id", SortDirection.DESC).first<Review>()
        assertIs<Review>(reviews.data)

        val review = reviews.data
        assertEquals("Review", review.type)
        assertEquals("fda4c1a4-6f91-4136-9398-2b4f8f4f74d9_155", review.id)
    }

    @Test
    fun include() = runTest {
        val reviews = client.includes("book", "reader", "book.publisher", "book.publisher.books").find<Review>("0ff9069c-d15f-4049-a228-68a3652fbbcf_21")
        assertIs<Review>(reviews.data)

        val review = reviews.data
        assertEquals("Review", review.type)
        assertEquals("0ff9069c-d15f-4049-a228-68a3652fbbcf_21", review.id)

        val reader = review.reader
        assertEquals("Person", reader.type)
        assertEquals("21", reader.id)

        val book = review.book
        assertEquals("0ff9069c-d15f-4049-a228-68a3652fbbcf", book.id)

        val publisher = book.publisher
        assertEquals("Publisher", publisher.type)
        assertEquals("11", publisher.id)

        val publishedBooks = review.book.publisher.books
        assertIs<List<Book>>(publishedBooks)
        assertEquals(1, publishedBooks.size)
        assertEquals("Book", publishedBooks[0].type)
        assertEquals("0ff9069c-d15f-4049-a228-68a3652fbbcf", publishedBooks[0].id)
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