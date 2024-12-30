package it.maicol07.spraypaintkt_test

import it.maicol07.spraypaintkt.JsonApiError
import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt_test.models.Publisher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ErrorsTest : BaseTest() {
    @Test(timeout = 100_000)
    fun `Test error handling`() = runTest {
        assertFails {
            Publisher.find(id = "nonexistent")
        }

        try {
            Publisher.find(id = "nonexistent")
        } catch (e: JsonApiException) {
            assertEquals(404, e.statusCode)

            assertNotNull(e.errors)
            assertIs<List<JsonApiError>>(e.errors)
            assertEquals(1, e.errors.size)

            val error = e.errors.first()

            assertEquals("404", error.code)
        }
    }
}