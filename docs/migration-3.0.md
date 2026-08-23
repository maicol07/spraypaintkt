# Migration to 3.0.0

## JSON:API response wrappers

`JsonApiResponse`, `JsonApiResource`, links and relationships are now serializable
wire DTOs. Identifiers and relationship cardinality are typed. Dynamic attributes
and metadata use `Map<String, JsonElement>` until the deserializer maps them to a
generated resource.

Links use `JsonApiLink.Simple` or `JsonApiLink.Details`. Relationship linkage uses
`JsonApiLinkage` to distinguish missing, empty to-one, to-one and to-many data.
The top-level `jsonapi` member is available as `JsonApiObject`.

Before:

```kotlin
val count = response.meta["count"] as Int
```

After:

```kotlin
val count = response.meta["count"]?.jsonPrimitive?.int
```

`CollectionProxy.meta` and `RecordProxy.meta` are also `Map<String, JsonElement>` values.
Resource attributes and model metadata remain Kotlin values; the deserializer
performs that conversion at the resource boundary.

## Ktor integration

Pass an existing Ktor client with the `httpClient` parameter. The wrapper does
not close an injected client; the caller owns it. A client created by
`KtorHttpClient` is closed by `close()`.

```kotlin
KtorHttpClient(httpClient = existingClient)
```

## Release and snapshots

Snapshot builds publish only to GitHub Packages. Maven Central is published by
the release workflow after a GitHub release is published.
