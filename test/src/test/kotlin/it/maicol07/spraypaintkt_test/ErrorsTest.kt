package it.maicol07.spraypaintkt_test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.maicol07.spraypaintkt.JsonApiError
import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt_test.models.Publisher

class ErrorsTest : FunSpec({
    test("Test error handling") {
        shouldThrow<JsonApiException> {
            Publisher.find(id = "nonexistent")
        }

        val e = shouldThrow<JsonApiException> {
            Publisher.find(id = "nonexistent")
        }
        e.statusCode shouldBe 404

        e.errors.shouldNotBeNull()
        e.errors.shouldBeInstanceOf<List<JsonApiError>>()
        e.errors.shouldHaveSize(1)

        val error = e.errors.first()

        error.code shouldBe "404"
    }
})

