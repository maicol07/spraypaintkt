package it.maicol07.spraypaintkt_test

import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.extensions.destroy
import it.maicol07.spraypaintkt.extensions.save
import it.maicol07.spraypaintkt_test.models.Person
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * NOTE: Since we don't have a fake API to test against, we won't use save methods in the tests.
 * Instead, we'll compare the payload sent to the server with the expected one.
 */
class WritingTests : BaseTest() {
    @Test
    fun writing() = runTest {
        var person = Person()
        person.name = "John Doe"
        person.email = "john@doe.com"
        person.comment = "This is a comment"
        person.dob = "1990-01-01"
        assertTrue { person.save() }
        assertNotNull(person.id)

        // Refresh the person object
        person = Person.find(person.id!!).data

        person.name = "Jane Doe"
        assertTrue { person.save() }

        val updatedResponse = Person.find(person.id!!)
        val updatedPerson = updatedResponse.data
        assertEquals("Jane Doe", updatedPerson.name)
        person = updatedPerson

        // Refresh the person object
        person = Person.find(person.id!!).data

        assertTrue { person.destroy() }

        try {
            Person.find(person.id!!)
        } catch (e: JsonApiException) {
            assertTrue { e.statusCode == 404 }
        }
    }
}