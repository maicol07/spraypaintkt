package it.maicol07.spraypaintkt_test

import io.ktor.http.quote
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.ResourceRegistry
import it.maicol07.spraypaintkt.registerResources
import it.maicol07.spraypaintkt_test.models.Book
import it.maicol07.spraypaintkt_test.models.Publisher
import it.maicol07.spraypaintkt_test.models.Review
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class SerializationTest: BaseTest() {
    @Test(timeout = 100_000)
    fun serializationTest() {
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

        assert(jsonApiString.isNotEmpty())
        assertEquals(minified, jsonApiString)

        val serializedJson = Json.encodeToString(book)
        assert(serializedJson.isNotEmpty())
        assertEquals(minified, serializedJson.replace("\\\"", "\"").trim('"'))
        println(serializedJson)
    }

    @Test(timeout = 100_000)
    fun deserializationTest() {
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
        assert(review.id == "01f6fecf-ccce-4f5b-8934-018faecf6186_75")
        assert(review.review == "review 37")
        assert(review.book.title == "book_title37")
        assert(review.book.publisher.name == "publisher37")
    }
}