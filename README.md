# Spraypaint.kt

> Inspired by [Spraypaint.JS](https://github.com/graphiti-api/spraypaint.js)

A Kotlin library for interacting with JSONAPI-compliant APIs.

# Installation

Add the following to the common dependencies of your `build.gradle.kts` file:
```kotlin
implementation("it.maicol07.spraypaintkt:core:$latest_version")
```

## Snapshots
You can find snapshots on [Github Packages](https://github.com/maicol07?tab=packages&repo_name=spraypaintkt).
To use them, you need to add the following to your `settings.gradle.kts` file in the `dependencyResolutionManagement` block:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/maicol07/spraypaintkt")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}
```
Then, you have to add your username and a personal access token to your `local.properties` file:
```properties
gpr.user=USERNAME
gpr.key=TOKEN
```
> [!NOTE]
> More info can be found [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

# Usage

## Configuration
### HTTP Client
You can use any HTTP client you want, as long as you wrap it in a `HttpClient` implementation.
The library provides a Ktor integration (see [Ktor Integration](#ktor))

```kotlin
class HttpClientImpl : HttpClient {
    // ...
}
```

### Model generator
Since Kotlin Multiplatform doesn't support reflection, you need create a class that implements `ModelGenerator` and returns the new instance of the model.
The library provides a Koin integration (see [Koin Integration](#koin))

```kotlin
class ModelGeneratorImpl : ModelGenerator {
    override fun <R: Resource> generate(clazz: KClass<R>): R {
        // your own implementation
    }
}
```

### Client creation
You'll now need to create a Client instance, which will be used to interact with the API:

```kotlin
val client = Client(
    httpClient = HttpClientImpl(),
    modelGenerator = ModelGeneratorImpl(),
    baseUrl = "https://api.example.com"
)
```

## Models
You can create your own models by extending the `Resource` class:

```kotlin
class User : Resource() {
}
```
### Adding attributes
You can add attributes to your model by appending the `by attributes` delegate to the property:

```kotlin
class User : Resource() {
    val name: String by attributes
    val email: String by attributes
}
```

If you wish to use a different property name than the one returned by the API, you can use the `attribute` delegate:

```kotlin
class User : Resource() {
    val name: String by attribute("full_name")
}
```

### Adding relationships
You can add relationships to your model by appending the `by relationships` delegate to the property:

```kotlin
class User : Resource() {
    val posts: List<Post> by relationships
    val comments: List<Comment> by relationships
    val membership: Membership by relationships
}
```
To-One relationships are automatically converted to the correct type, while To-Many relationships are always converted to a `List` (with the model type in generics).

> [!IMPORTANT]
> You need to register the related models in the `Client` instance and include them in the request to be able to resolve the relationships (see below).

If you wish to use a different property name than the one returned by the API, you can use the `relationship` and `hasManyRelationship` delegate:

```kotlin
class User : Resource() {
    val posts: List<Post> by hasManyrelationship("user_posts")
    val comments: List<Comment> by hasManyrelationship("user_comments")
    val membership: Membership by relationship("user_membership")
}
```

### Customizing the model
You can customize the model by setting the `type` and `endpoint` properties when extending the `Resource` class:

```kotlin
class User : Resource(type = "users", endpoint = "models/users") {
}
```

### Registering models
To be able to resolve the relationships, you need to register the models in the `Client` instance:

```kotlin
client.registerResource<User>()

// You can also register a custom type instead of the model one
client.registerResource<User>("users")
```

## Querying
You can query the API using the `Client` instance:

```kotlin
// Find the user with ID = 1
val response = client.find<User>(1)
val user = response.data

// Fetch all the users
val response = client.all<User>()
val users = response.data

// Fetch the first user in the response
val response = client.first<User>()
val user = response.data
```

### Filtering
You can filter the results using the `where` method:

```kotlin
val response = client.where("name", "John").all<User>()
val user = response.data

// You can also use the `where` method multiple times
val response = client.where("name", "John").where("email", "john@doe.com").all<User>()
val user = response.data
```

### Sorting
You can sort the results using the `order` method:

```kotlin
val response = client.order("name", SortDirection.DESC).all<User>()
val user = response.data
```

### Pagination
You can paginate the results using the `per` and `page` methods:

```kotlin
val response = client.per(10).page(2).all<User>()
val user = response.data
```

If you're using offset-based pagination, you have to change the pagination strategy when creating the client and use the `offset` and `limit` methods:

```kotlin
val client = Client(
    httpClient = HttpClientImpl(),
    modelGenerator = ModelGeneratorImpl(),
    baseUrl = "https://api.example.com",
    paginationStrategy = OffsetBasedPaginationStrategy()
)
val response = client.offset(10).limit(50).all<User>()
val user = response.data
```

### Including relationships
You can include the relationships using the `includes` method:

```kotlin
val response = client.includes("posts", "comments").all<User>()
val user = response.data
```

> [!CAUTION]
> Only if you include the relationships in the request, the library will be able to resolve them in your model.


## Creating
You can create a new resource creating a new resource, filling all the attributes and relationships and then calling the `save` method:

```kotlin
val user = User()
user.name = "John"
user.email = "john@doe.com"
val result = client.save(user)
if (result) {
    // The resource has been created
}
```

## Updating
You can update a resource by modifying the attributes and relationships and then calling the `save` method:

```kotlin
val user = client.find<User>(1).data
user.name = "John Doe"
val result = client.save(user)
if (result) {
    // The resource has been updated
}
```

## Deleting
To delete a resource, you can use the `destroy` method:

```kotlin
val result = client.destroy(user)
if (result) {
    // The resource has been deleted
}
```

# Integrations
## Ktor
The library provides a Ktor integration, which allows you to use the `HttpClient` implementation provided by Ktor.

Add the following to the common dependencies of your `build.gradle.kts` file:
```kotlin
implementation("it.maicol07.spraypaintkt:ktor-integration:$latest_version")
```

You can now create a `Client` instance using the `KtorHttpClient`:

```kotlin
val client = Client(
    httpClient = KtorHttpClient(),
    modelGenerator = ModelGeneratorImpl(),
    baseUrl = "https://api.example.com"
)
```

### Ktor Client Configuration
You can configure the Ktor client by passing a `HttpClientConfig` instance to the `KtorHttpClient` constructor:

```kotlin
val client = Client(
    httpClient = KtorHttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    },
    modelGenerator = ModelGeneratorImpl(),
    baseUrl = "https://api.example.com"
)
```

### Custom Ktor Client
You can use a custom Ktor client by passing it to the `KtorHttpClient` constructor:

```kotlin
val client = Client(
    httpClient = KtorHttpClient(
        client = HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    ),
    modelGenerator = ModelGeneratorImpl(),
    baseUrl = "https://api.example.com"
)
```

## Koin
The library provides a Koin integration, which allows you to use the `ModelGenerator` implementation provided by Koin.
Models will be resolved using the Koin container, but you need to add them manually in a module.

Add the following to the common dependencies of your `build.gradle.kts` file:
```kotlin
implementation("it.maicol07.spraypaintkt:koin-integration:$latest_version")
```

You can now create a `Client` instance using the `KoinModelGenerator`:

```kotlin
val client = Client(
    httpClient = HttpClientImpl(),
    modelGenerator = KoinModelGenerator(),
    baseUrl = "https://api.example.com"
)
```

You need to add the models to the Koin container:

```kotlin
val modelsModule = module {
    single { User() }
    single { Post() }
    single { Comment() }
    single { Membership() }
}
```