package it.maicol07.spraypaintkt.tests

import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.tests.models.Tag
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ErrorsTest : BaseTest() {
    @Test
    fun `Test error handling`() = runTest {
        assertFails {
            client.find<Tag>(id = "nonexistent")
        }

        try {
            client.find<Tag>(id = "nonexistent")
        } catch (e: JsonApiException) {
            assertEquals(404, e.statusCode)

            assertNotNull(e.errors)
            assertEquals(1, e.errors.size)
            assertIs<List<*>>(e.errors)

            val error = e.errors.first()

            assertEquals(404, error.status)
            assertEquals("not_found", error.code)
        }
    }
}