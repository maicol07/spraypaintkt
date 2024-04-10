package it.maicol07.spraypaintkt.tests

import it.maicol07.spraypaintkt.JsonApiErrorResponse
import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.tests.models.Publisher
import it.maicol07.spraypaintkt.tests.models.Review
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ErrorsTest : BaseTest() {
    @Test
    fun `Test error handling`() = runTest {
        assertFails {
            client.find<Publisher>(id = "nonexistent")
        }

        try {
            client.find<Publisher>(id = "nonexistent")
        } catch (e: JsonApiException) {
            assertEquals(404, e.statusCode)

            assertNotNull(e.errors)
//            assertEquals(1, e.errors.size)
//            assertIs<List<*>>(e.errors)
//
//            val error = e.errors.first()
//
//            assertEquals(404, error.status)
        }
    }
}