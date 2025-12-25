package it.maicol07.spraypaintkt_test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.http.quote
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.BookGenre
import it.maicol07.spraypaintkt_test.models.Publisher
import it.maicol07.spraypaintkt_test.models.Review
import kotlinx.serialization.json.Json

class SerializationTest : FunSpec({
    test("serializationTest") {
        val book = Book()
        book.title = "book_title37"
        book.publisher = Publisher().apply {
            id = "38"
        }

        val jsonApiString = book.toJsonApiString()
        val shouldBe = """
            {
              "data": {
                "type": "Book",
                "attributes": {
                  "title": "book_title37"
                },
                "relationships": {
                  "publisher": {
                    "data": {
                      "type": "Publisher",
                      "id": "38"
                    }
                  }
                }
              },
              "included": [
                {
                  "type": "Publisher",
                  "id": "38",
                  "attributes":{},
                  "relationships":{}
                }
              ]
            }
        """.trimIndent()
        val jsonMinified = Json { prettyPrint = false }
        val minified = jsonMinified.encodeToString(Json.parseToJsonElement(shouldBe))

        jsonApiString.shouldNotBeEmpty()
        jsonApiString shouldBe minified

        val serializedJson = Json.encodeToString(book)
        serializedJson.shouldNotBeEmpty()
        serializedJson.replace("\\\"", "\"").trim('"') shouldBe minified
        println(serializedJson)
    }

    test("deserializationTest") {
        // Initialize model or call the initializer manually
        Book
        // or ResourceRegistry.registerResources()

        val jsonApiString = """
            {
              "data": {
                "attributes": {
                  "book_id": "01f6fecf-ccce-4f5b-8934-018faecf6186",
                  "created": "2024-07-09 18:38:08.149999",
                  "reader_id": 75,
                  "review": "review 37"
                },
                "id": "01f6fecf-ccce-4f5b-8934-018faecf6186_75",
                "links": {
                  "self": "/api/Reviews/01f6fecf-ccce-4f5b-8934-018faecf6186_75/"
                },
                "relationships": {
                  "book": {
                    "data": {
                      "id": "01f6fecf-ccce-4f5b-8934-018faecf6186",
                      "type": "Book"
                    },
                    "links": {
                      "self": "/api/Reviews/01f6fecf-ccce-4f5b-8934-018faecf6186_75/book"
                    }
                  },
                  "reader": {
                    "data": null,
                    "links": {
                      "self": "/api/Reviews/01f6fecf-ccce-4f5b-8934-018faecf6186_75/reader"
                    }
                  }
                },
                "type": "Review"
              },
              "included": [
                {
                  "attributes": {
                    "author_id": 76,
                    "published": null,
                    "publisher_id": 38,
                    "reader_id": 75,
                    "title": "book_title37"
                  },
                  "id": "01f6fecf-ccce-4f5b-8934-018faecf6186",
                  "links": {
                    "self": "/api/Books/01f6fecf-ccce-4f5b-8934-018faecf6186/"
                  },
                  "relationships": {
                    "author": {
                      "data": null,
                      "links": {
                        "self": "/api/Books/01f6fecf-ccce-4f5b-8934-018faecf6186/author"
                      }
                    },
                    "publisher": {
                      "data": {
                        "id": "38",
                        "type": "Publisher"
                      },
                      "links": {
                        "self": "/api/Books/01f6fecf-ccce-4f5b-8934-018faecf6186/publisher"
                      }
                    },
                    "reader": {
                      "data": null,
                      "links": {
                        "self": "/api/Books/01f6fecf-ccce-4f5b-8934-018faecf6186/reader"
                      }
                    },
                    "reviews": {
                      "data": [],
                      "links": {
                        "self": "/api/Books/01f6fecf-ccce-4f5b-8934-018faecf6186/reviews"
                      }
                    }
                  },
                  "type": "Book"
                },
                {
                  "attributes": {
                    "custom_field": "some customization",
                    "name": "publisher37",
                    "stock": 100
                  },
                  "id": "38",
                  "links": {
                    "self": "/api/Publishers/38/"
                  },
                  "relationships": {
                    "books": {
                      "data": [],
                      "links": {
                        "self": "/api/Publishers/38/books"
                      }
                    }
                  },
                  "type": "Publisher"
                }
              ],
              "jsonapi": {
                "version": "1.0"
              },
              "links": {
                "related": "/api/Reviews/01f6fecf-ccce-4f5b-8934-018faecf6186_75?include=book,book.publisher",
                "self": "/api/Reviews/01f6fecf-ccce-4f5b-8934-018faecf6186_75/"
              },
              "meta": {
                "count": 1,
                "instance_meta": {},
                "limit": 250,
                "total": 1
              }
            }
        """.trimIndent().quote()

        val review = Json.decodeFromString<Resource>(jsonApiString) as Review
        review.id shouldBe "01f6fecf-ccce-4f5b-8934-018faecf6186_75"
        review.review shouldBe "review 37"
        review.book.title shouldBe "book_title37"
        review.book.publisher.name shouldBe "publisher37"
    }

    test("enumSerializationTest") {
        val book = Book()
        book.title = "Dune"
        book.genre = BookGenre.SCIENCE_FICTION

        val jsonApiString = book.toJsonApiString()
        val shouldBe = """
            {
              "data": {
                "type": "Book",
                "attributes": {
                  "title": "Dune",
                  "genre": "SCIENCE_FICTION"
                },
                "relationships": {}
              },
              "included": []
            }
        """.trimIndent()
        val jsonMinified = Json { prettyPrint = false }
        val minified = jsonMinified.encodeToString(Json.parseToJsonElement(shouldBe))

        jsonApiString.shouldNotBeEmpty()
        jsonApiString shouldBe minified
    }

    test("enumDeserializationTest") {
        Book // Initialize the resource

        val jsonApiString = """
            {
              "data": {
                "type": "Book",
                "id": "1",
                "attributes": {
                  "title": "The Hobbit",
                  "publisher_id": 1,
                  "genre": "FANTASY"
                },
                "relationships": {}
              }
            }
        """.trimIndent().quote()

        val book = Json.decodeFromString<Resource>(jsonApiString) as Book
        book.id shouldBe "1"
        book.title shouldBe "The Hobbit"
        book.genre shouldBe BookGenre.FANTASY
    }
})
