package it.maicol07.spraypaintkt_openapi

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.nio.file.Path

private fun fixture(name: String): String =
    Path.of(OpenApiSchemaGenerationTest::class.java.getResource("/$name")!!.toURI()).toString()

class OpenApiSchemaGenerationTest : StringSpec({
    val document = OpenApiResourceParser.parse(fixture("jsonapi-books.yaml"))
    val definitions = document.resources
    val sources = SchemaFileGenerator("com.example.models").generate(definitions)

    "detects the pinned-type dialect" {
        document.dialect shouldBe OpenApiDialect.PINNED_TYPE
    }

    "recovers every resource object exactly once" {
        definitions.map { it.resourceType } shouldContainExactly listOf("OpenApiBook", "OpenApiPerson", "OpenApiReview")
    }

    "derives the endpoint from the collection path and falls back to the resource type" {
        definitions.associate { it.resourceType to it.endpoint } shouldBe mapOf(
            "OpenApiBook" to "openapi-books",
            "OpenApiPerson" to "openapi-people",
            "OpenApiReview" to "OpenApiReview",
        )
    }

    "keeps relationship cardinality from the linkage shape" {
        val book = definitions.single { it.resourceType == "OpenApiBook" }
        book.relations.associate { it.propertyName to (it.toMany to it.nullable) } shouldBe mapOf(
            "author" to (false to false),
            "reader" to (false to true),
            "reviews" to (true to false),
        )
    }

    "emits a schema per resource" {
        sources.keys shouldContainExactly setOf("OpenApiBookSchema.kt", "OpenApiPersonSchema.kt", "OpenApiReviewSchema.kt")
    }

    "generates a schema the KSP processor can consume" {
        withClue(sources.getValue("OpenApiBookSchema.kt")) {
            sources.getValue("OpenApiBookSchema.kt") shouldBe EXPECTED_BOOK_SCHEMA
        }
    }
})

private val EXPECTED_BOOK_SCHEMA = """
    package com.example.models

    import it.maicol07.spraypaintkt_annotation.Attr
    import it.maicol07.spraypaintkt_annotation.Relation
    import it.maicol07.spraypaintkt_annotation.ResourceSchema
    import kotlin.Any
    import kotlin.Boolean
    import kotlin.Float
    import kotlin.Int
    import kotlin.String
    import kotlin.collections.List
    import kotlin.collections.Map

    @ResourceSchema(
        resourceType = "OpenApiBook",
        endpoint = "openapi-books",
    )
    public interface OpenApiBookSchema {
        @Attr
        public var title: String

        @Attr
        public var pageCount: Int

        @Attr
        public var publisherId: Int?

        @Attr
        public var rating: Float?

        @Attr
        public var published: Boolean?

        @Attr
        public var tags: List<String>?

        @Attr
        public var metadata: Map<String, Any?>?

        @Relation
        public val author: OpenApiPersonSchema

        @Relation
        public val reader: OpenApiPersonSchema?

        @Relation
        public val reviews: List<OpenApiReviewSchema>
    }

""".trimIndent()
