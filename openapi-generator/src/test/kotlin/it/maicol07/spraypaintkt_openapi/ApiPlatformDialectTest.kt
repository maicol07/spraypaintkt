package it.maicol07.spraypaintkt_openapi

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.nio.file.Path

private fun fixture(name: String): String =
    Path.of(ApiPlatformDialectTest::class.java.getResource("/$name")!!.toURI()).toString()

/**
 * API Platform leaves `type` a free-form string, so the resource type comes from the component name
 * and relationship targets come from the envelope's `included`.
 */
class ApiPlatformDialectTest : StringSpec({
    val document = OpenApiResourceParser.parse(fixture("apiplatform-jsonapi.json"))
    val definitions = document.resources
    val sources = SchemaFileGenerator("com.example.models").generate(definitions)
    val libraryEntry = definitions.single { it.resourceType == "LibraryEntry" }

    "detects the component-name dialect" {
        document.dialect shouldBe OpenApiDialect.COMPONENT_NAME
    }

    /**
     * The fixture carries all ten components API Platform emits. Only the two `.jsonapi` envelopes
     * are resource objects: the flat `.html`/`.jsonMergePatch`/bare variants have no `attributes`
     * member, and the pagination base schemas have a `data` array without `items`.
     */
    "names resources after the component and skips the other representations" {
        definitions.map { it.resourceType } shouldContainExactly listOf("LibraryEntry", "User")
    }

    "falls back to the operation tag when the collection payload is inline" {
        definitions.associate { it.resourceType to it.endpoint } shouldBe mapOf(
            "LibraryEntry" to "api/library_entries",
            "User" to "api/users",
        )
    }

    "resolves a relationship target through the envelope's included list" {
        libraryEntry.relations.single().let {
            it.targetResourceType shouldBe "User"
            it.toMany shouldBe false
            // The linkage is `oneOf: [null, {type, id}]`.
            it.nullable shouldBe true
        }
    }

    "reads nullability from an OpenAPI 3.1 type union and mutability from readOnly" {
        libraryEntry.attributes.associate { it.propertyName to it.mutable } shouldBe mapOf(
            "gameId" to true, "status" to true, "completionStatus" to true, "owned" to true,
            "platformsIds" to true, "startDate" to true, "endDate" to true, "playedTime" to true,
            "rating" to true, "ratingDetails" to true, "review" to true,
            "createdAt" to false, "updatedAt" to false,
            "editionsIds" to true, "userId" to true,
        )
        libraryEntry.attributes.all { it.nullable } shouldBe true
    }

    "turns closed string enums into Kotlin enums" {
        libraryEntry.attributes.mapNotNull { it.enum }.associate { it.className to it.values } shouldBe mapOf(
            "LibraryEntryStatus" to listOf("PLAYING", "COMPLETED", "PAUSED", "ABANDONED", "BACKLOG"),
            // The `null` member of the enum is dropped: nullability is carried by the type union.
            "LibraryEntryCompletionStatus" to listOf("MAIN_STORY", "MAIN_PLUS_SIDES", "FULL_100"),
        )
    }

    "emits the enums next to the schema that uses them" {
        withClue(sources.getValue("LibraryEntrySchema.kt")) {
            sources.getValue("LibraryEntrySchema.kt") shouldBe EXPECTED_LIBRARY_ENTRY_SCHEMA
        }
    }
})

private val EXPECTED_LIBRARY_ENTRY_SCHEMA = """
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

    @ResourceSchema(
        resourceType = "LibraryEntry",
        endpoint = "api/library_entries",
    )
    public interface LibraryEntrySchema {
        @Attr
        public var gameId: Int?

        @Attr
        public var status: LibraryEntryStatus?

        @Attr
        public var completionStatus: LibraryEntryCompletionStatus?

        @Attr
        public var owned: Boolean?

        @Attr
        public var platformsIds: List<Any?>?

        @Attr
        public var startDate: String?

        @Attr
        public var endDate: String?

        @Attr
        public var playedTime: Int?

        @Attr
        public var rating: Float?

        @Attr
        public var ratingDetails: List<Any?>?

        @Attr
        public var review: String?

        @Attr(mutable = false)
        public val createdAt: String?

        @Attr(mutable = false)
        public val updatedAt: String?

        @Attr
        public var editionsIds: List<Any?>?

        @Attr
        public var userId: Any?

        @Relation
        public val user: UserSchema?
    }

    public enum class LibraryEntryStatus {
        PLAYING,
        COMPLETED,
        PAUSED,
        ABANDONED,
        BACKLOG,
    }

    public enum class LibraryEntryCompletionStatus {
        MAIN_STORY,
        MAIN_PLUS_SIDES,
        FULL_100,
    }

""".trimIndent()
