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

## Android artifacts

The Android targets moved to `com.android.kotlin.multiplatform.library` (AGP 9).
Each library now publishes a single Android artifact instead of one per build
type.

This lands after `3.0.0-rc1`, so it also affects builds already pinned to that
release candidate.

| Up to and including `3.0.0-rc1` | After |
| --- | --- |
| `core-android-debug`, `core-android-release` | `core-android` |
| `annotation-android-debug`, `annotation-android-release` | `annotation-android` |
| `ktor-integration-android-debug`, `ktor-integration-android-release` | `ktor-integration-android` |

If you depend on the root coordinates you do not need to change anything.
Gradle module metadata resolves the Android variant for you:

```kotlin
implementation("it.maicol07.spraypaintkt:core:$version")
```

Only pinned platform artifacts break. Replace the build-type suffix:

Before:

```kotlin
implementation("it.maicol07.spraypaintkt:core-android-release:$version")
```

After:

```kotlin
implementation("it.maicol07.spraypaintkt:core-android:$version")
```

The published AAR still declares `minSdkVersion 26` and no `targetSdkVersion`.

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
