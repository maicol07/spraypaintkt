package it.maicol07.spraypaintkt.tests

import it.maicol07.spraypaintkt.SortDirection
import it.maicol07.spraypaintkt.tests.models.Discussion
import it.maicol07.spraypaintkt.tests.models.Tag
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class QueryTest : BaseTest() {
    @Test
    fun all() = runTest {
        val discussions = client.all<Discussion>()
        assertIs<List<Discussion>>(discussions.data)

        val discussion = discussions.data.first()
        assertEquals("discussions", discussion.type)
    }

    @Test
    fun find() = runTest {
        val discussion = client.find<Discussion>("32901")

        assertIs<Discussion>(discussion.data)

        val resource = discussion.data
        assertEquals("discussions", resource.type)
        assertEquals("32901", resource.id)
        assertEquals("Staff Diary: v2.0 Cycle", discussion.data.title)
    }

    @Test
    fun first() = runTest {
        val discussion = client.first<Discussion>()
        assertEquals("discussions", discussion.data.type)
    }

    @Test
    fun filter() = runTest {
        val discussion = client.where("title", "Staff Diary: v2.0 Cycle").first<Discussion>()
        assertIs<Discussion>(discussion.data)

        assertEquals("Staff Diary: v2.0 Cycle", discussion.data.title)
        assertEquals("32901", discussion.data.id)
    }

    @Test
    fun sort() = runTest {
        val discussions = client.order("createdAt", SortDirection.ASC).first<Discussion>()
        assertIs<Discussion>(discussions.data)

        val discussion = discussions.data
        assertEquals("discussions", discussion.type)
        assertEquals("4", discussion.id)
    }

    @Test
    fun include() = runTest {
        val discussions = client.includes("user", "tags").find<Discussion>("32901")
        assertIs<Discussion>(discussions.data)

        val discussion = discussions.data
        assertEquals("discussions", discussion.type)
        assertEquals("32901", discussion.id)

        val user = discussion.user
        assertEquals("1376", user?.id)

        val tags = discussion.tags
        assertIs<List<Tag>>(tags)
        assertEquals(2, tags.size)

        for (tag in tags) {
            assertEquals("tags", tag.type)
        }

        assertEquals("7", tags[0].id)
        assertEquals("16", tags[1].id)
    }

    @Test
    fun page() = runTest {
        val discussions = client.offset(1).limit(3).all<Discussion>()
        assertIs<List<Discussion>>(discussions.data)
        assertEquals(3, discussions.data.size)

        val discussion = discussions.data.first()
        assertEquals("discussions", discussion.type)
    }

    @Test
    fun fields() = runTest {
        val discussions = client.select("discussions", "title", "slug").first<Discussion>()

        val discussion = discussions.data
        assertEquals("discussions", discussion.type)
        assertNotNull(discussion.title)
        assertNotNull(discussion.slug)
//        assertNull(discussion.commentCount)
        assertFails { discussion.commentCount }
    }
}