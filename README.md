# Spraypaint.kt

> Inspired by [Spraypaint.JS](https://github.com/graphiti-api/spraypaint.js)

> [!NOTE]
> This README is meant for Spraypaint.kt 2.0.0 and above. For the previous version, see the [1.0.0 tag](https://github.com/maicol07/spraypaintkt/tree/1.0.0).

A Kotlin library for interacting with JSONAPI-compliant APIs.

# Installation

## Android/Jvm only
Add the following to your `build.gradle.kts` file:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

dependencies {
    implementation("it.maicol07.spraypaintkt:core:$latest_version")
    implementation("it.maicol07.spraypaintkt:annotation:$latest_version")
    ksp("it.maicol07.spraypaintkt:processor:$latest_version")
}
```

## Multiplatform
Add the following to your `build.gradle.kts` file:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("it.maicol07.spraypaintkt:core:$latest_version")
            implementation("it.maicol07.spraypaintkt:annotation:$latest_version")
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", "it.maicol07.spraypaintkt:processor:$latest_version")
}

// Workaround for KSP only in Common Main.
// https://github.com/google/ksp/issues/567
tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}
```

## Snapshots
You can find snapshots on [Github Packages](https://github.com/maicol07?tab=packages&repo_name=spraypaintkt).
To use them, you need to add the following to your `settings.gradle.kts` file:
```kotlin
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/maicol07/spraypaintkt")
            credentials {
                username = project.findProperty("ghpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("ghpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}
```
Then, you have to add your username and a personal access token to your `local.properties` file:
```properties
ghpr.user=USERNAME
ghpr.key=TOKEN
```
> [!NOTE]
> More info can be found [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

# Usage
## Configuration
Create a configuration object that implements the `JsonApiConfig` interface and mark it with the `@DefaultInstance` annotation
(this way the default instance will be used for every resource when not specified).
This object will contain the base URL of the API, the pagination strategy, and the HTTP client to use.
You can use any HTTP client you want, as long as you wrap it in a `HttpClient` implementation.
The library provides a Ktor integration (see [Ktor Integration](#ktor))

Example:
```kotlin
@DefaultInstance
data object AppJsonApiConfig: JsonApiConfig {
    override val baseUrl: String = "https://safrs.onrender.com/api"
    override val paginationStrategy: PaginationStrategy = PaginationStrategy.OFFSET_BASED
    override val httpClient: HttpClient = KtorHttpClient()
}
```

## Defining resources
You can define your resources by creating an interface that ends with `Schema` and annotating it with the `@ResourceSchema` annotation.
The annotation requires the `resourceType` and `endpoint` properties to be set, but you can also set the configuration object to use (if not the default one).
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
interface BookSchema {}
```

### Adding attributes
You can add attributes to your resource schema by appending the `@Attr` annotation to an interface property:
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
interface BookSchema {
    @Attr val title: String
}
```

If you wish to use a different property name than the one returned by the API, you can set the attribute name returned by the API in the `@Attr` annotation:
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
interface BookSchema {
    @Attr("my_title") val title: String
}
```

By default, the library will convert property names to snake_case when searching for the attribute in the JSONAPI response.
You can toggle this behavior by setting the `autoTransform` property in the `@ResourceSchema` annotation.
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
interface BookSchema {
    @Attr(autoTransform = true) val title: String
}
```

You can set a default value for the attribute by setting a getter for the property:
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
interface BookSchema {
    @Attr val title: String
        get() = "Default title"
}
```

### Adding relationships
You can add relationships to your model by appending the `@ToOneRelationship` or `@ToManyRelationship` annotation to an interface property.
The annotation requires the `relationship` property to be set to the relationship name returned by the API and the related ResourceSchema class.
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
@ToManyRelationship("reviews", ReviewSchema::class)
@ToOneRelationship("publisher", PublisherSchema::class)
@ToOneRelationship("author", PersonSchema::class)
@ToOneRelationship("reader", PersonSchema::class)
interface BookSchema {
    @Attr val title: String
}
```
To-One relationships are automatically converted to the correct type, while To-Many relationships are always converted to a `List` (with the model type in generics).

If you wish to use a different property name than the one returned by the API, you can set the relationship name returned by the API in the `@ToOneRelationship` or `@ToManyRelationship` annotation:
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
@ToManyRelationship("reviews", ReviewSchema::class, propertyName = "my_reviews")
@ToOneRelationship("publisher", PublisherSchema::class)
@ToOneRelationship("author", PersonSchema::class)
@ToOneRelationship("reader", PersonSchema::class)
interface BookSchema {
    @Attr val title: String
}
```

You can set a default value for the relationship by adding a function to the interface with the following name: `default<Relationship>` (where `<Relationship>` is the relationship name with the first letter capitalized). The returned value will be used as the default value for the relationship.
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
@ToManyRelationship("reviews", ReviewSchema::class)
@ToOneRelationship("publisher", PublisherSchema::class)
@ToOneRelationship("author", PersonSchema::class)
@ToOneRelationship("reader", PersonSchema::class, canBeEmpty = true)
interface BookSchema {
    @Attr val title: String
    
    fun defaultReader(): Person? {
        return null
    }
}
```

#### Nullable relationships
By default, relationships are not nullable. If you want to make a relationship nullable, you can set the `canBeEmpty` property in the `@ToOneRelationship` to `true`.
```kotlin
@ResourceSchema(resourceType = "Book", endpoint = "Books")
@ToManyRelationship("reviews", ReviewSchema::class)
@ToOneRelationship("publisher", PublisherSchema::class)
@ToOneRelationship("author", PersonSchema::class)
@ToOneRelationship("reader", PersonSchema::class, canBeEmpty = true)
interface BookSchema {
    @Attr val title: String
}
```
When empty, to-many relationships will be an empty list, while to-one relationships will be `null`. However, if you need to have `null` as possible value for a To-Many relationship, you can use the `canBeNull` property in the `@ToManyRelationship` annotation.

### Registering resources
To be able to resolve the relationships, you need to register the resources. When you use the generated resources class, these are automatically registered.
But when you aren't using them directly, such as when deserializing instances, you need to register them manually by calling the `registerResources` method on the `ResourcesRegistry` object.
```kotlin
ResourcesRegistry.registerResources()
```

## Generating resources
From now on, resources are automatically generated during the build process when schemas have been changed and you should use these classes to interact with the API.
These classes implement the resource schema interfaces you defined and they are named like your schema interface without the `Schema` suffix
and they have the same package as the schema interface.

## Querying
```kotlin
// Find the book with ID = 1
val response = Book.find(1)
val user = response.data

// Fetch all the books
val response = Book.all()
val users = response.data

// Fetch the first book in the response
val response = Book.first()
val user = response.data
```

### Filtering
You can filter the results using the `where` method:
```kotlin
val response = Book.where("title", "Journey to the Center of the Earth").all<User>()
val user = response.data

// You can also use the `where` method multiple times
val response = Book.where("name", "Journey to the Center of the Earth").where("email", "john@doe.com").all()
val user = response.data
```

### Sorting
You can sort the results using the `order` method:
```kotlin
val response = Book.order("name", SortDirection.DESC).all()
val user = response.data
```

### Pagination
You can paginate the results using the `per` and `page` methods:
```kotlin
val response = Book.per(10).page(2).all()
val user = response.data
```

If you're using offset-based pagination, you have to change the pagination strategy when creating the client and use the `offset` and `limit` methods:
```kotlin
val response = Book.offset(10).limit(50).all()
val user = response.data
```

### Including relationships
You can include the relationships using the `includes` method:
```kotlin
val response = Book.includes("reviews", "author").all()
val user = response.data
```

> [!CAUTION]
> Only if you include the relationships in the request, the library will be able to resolve them in your model.

## Creating
You can create a new resource using its constructor, filling all the attributes and relationships and then calling the `save` method:

> [!TIP]
> Be sure to use the generated resource class, not the schema interface.

```kotlin
val book = Book()
book.title = "Journey to the Center of the Earth"
val result = book.save()
if (result) {
    // The resource has been created
}
```

## Updating
You can update a resource by modifying the attributes and relationships and then calling the `save` method:

```kotlin
val book = Book.find(1).data
book.title = "Harry Potter and the Philosopher's Stone"
val result = client.save(user)
if (result) {
    // The resource has been updated
}
```

## Deleting
To delete a resource, you can use the `destroy` method:

```kotlin
val result = book.destroy()
if (result) {
    // The resource has been deleted
}
```

# Exceptions
The library provides a `JsonApiException` class, which is thrown when the JSONAPI server returns an error.
You can catch it and handle it as you want:

```kotlin
try {
    val book = Book.find(1)
} catch (e: JsonApiException) {
    // Handle the exception
    println(e.errors)
}
```

# Integrations
## Ktor
The library provides a Ktor integration, which allows you to use the `HttpClient` implementation provided by Ktor.

Add the following to the dependencies (or commonDependencies) block of your `build.gradle.kts` file:
```kotlin
implementation("it.maicol07.spraypaintkt:ktor-integration:$latest_version")
```

You can now create a `Client` instance using the `KtorHttpClient`:

```kotlin
@DefaultInstance
data object AppJsonApiConfig: JsonApiConfig {
    override val baseUrl: String = "https://api.example.com"
    override val paginationStrategy: PaginationStrategy = PaginationStrategy.OFFSET_BASED
    override val httpClient: HttpClient = KtorHttpClient()
}
```

### Ktor Client Configuration
You can configure the Ktor client by passing a `HttpClientConfig` instance to the `KtorHttpClient` constructor:

```kotlin
@DefaultInstance
data object AppJsonApiConfig: JsonApiConfig {
    override val baseUrl: String = "https://api.example.com"
    override val paginationStrategy: PaginationStrategy = PaginationStrategy.OFFSET_BASED
    override val httpClient: HttpClient = KtorHttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
}
```

### Custom Ktor Client
You can use a custom full Ktor client by passing it to the `KtorHttpClient` constructor:

```kotlin

@DefaultInstance
data object AppJsonApiConfig: JsonApiConfig {
    override val baseUrl: String = "https://api.example.com"
    override val paginationStrategy: PaginationStrategy = PaginationStrategy.OFFSET_BASED
    override val httpClient: HttpClient = KtorHttpClient(
        client = HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    )
}
```