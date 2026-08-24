# Generating schemas from an OpenAPI document

`:openapi-generator` turns a JSON:API-flavoured OpenAPI 3.0/3.1 document into `*Schema.kt` sources.
The KSP processor then consumes them exactly as if they had been written by hand, so the generated
files are meant to be checked into the repository and edited when the inference gets something wrong.

## Running it

```shell
./gradlew :openapi-generator:generateSchemas \
  -Pinput=https://api.example.com/openapi.yaml \
  -Ppackage=com.example.models \
  -Poutput=app/src/commonMain/kotlin
```

`input` accepts a local path (resolved against the repository root) or an HTTP(S) URL. Files are
overwritten on every run; resources that disappear from the document are not deleted.

The same entrypoint is available as a plain class for other build systems:

```kotlin
SpraypaintSchemaGenerator.generate(location, packageName, outputDirectory)
```

## Dialects

**There is no standard OpenAPI description of JSON:API.** JSON:API 1.1 specifies the wire format
only, so every generator invents its own encoding. Two are supported, detected automatically and
reported on stdout (`Read … as PINNED_TYPE: Book, Person`):

- **`PINNED_TYPE`** — `type` is pinned to a single `enum`/`const` value. Emitted by jsonapi.rb,
  Elide and Drupal JSON:API. The resource type and every relationship target are read straight from
  the document.
- **`COMPONENT_NAME`** — `type` is a free-form string and the resource type is the component name up
  to the first `.` (`LibraryEntry.jsonapi` → `LibraryEntry`). This is what API Platform emits, and
  its `ItemNormalizer` serializes `type` as that same short name verbatim, without dasherizing,
  pluralizing or lowercasing it.

The document is scanned once for pinned types; if none are found, `COMPONENT_NAME` is used. Under
`PINNED_TYPE`, a resource object that fails to pin its own type is an error rather than a fallback.

A component is recognised as a resource object when it has an `attributes` or `relationships` member,
with or without the surrounding `data` envelope. `allOf` chains are flattened and `$ref`s are
followed by the generator itself, because component names carry information the parser would
otherwise discard.

## Relationships

Cardinality comes from the linkage shape only — `data: {type: array}` → to-many — never from the
relationship name. A `oneOf`/`anyOf` wrapper is unwrapped to its single non-null alternative, and a
`{"type": "null"}` alternative makes a to-one linkage nullable; so does omission from `required`.
To-many linkages are always a non-null `List`.

The target resource is resolved in this order:

1. the identifier's own pinned `type`;
2. the component the identifier `$ref`s;
3. under `COMPONENT_NAME`, the single candidate in the envelope's `included.items.anyOf`;
4. under `COMPONENT_NAME`, a case-insensitive match of the relationship name against a known type
   (`user` → `User`).

Anything still unresolved raises `OpenApiSchemaException` listing the `included` candidates. Steps 3
and 4 are heuristics: with several relationships on one resource, check them.

If a generated `resourceType` collides with one already registered in the consuming project,
registration resolves to whichever schema registers first — check for collisions before generating
into a project that already declares resources with those types.

`resourceType` and `endpoint` are always emitted explicitly. The endpoint is the shortest
parameterless path that serves the resource, found by inspecting the `GET` response payload and then
by operation tag — API Platform leaves collection payloads inline but tags every operation with the
short name. It falls back to the resource type itself when neither matches, so verify those.

## Attribute types

Types follow what the deserializer puts into `Resource.attributes`, not what the document
advertises, because generated getters cast the stored value:

| OpenAPI                 | Kotlin                |
|-------------------------|-----------------------|
| `string`                | `String`              |
| `boolean`               | `Boolean`             |
| `integer`               | `Int`                 |
| `number`                | `Float`               |
| `array`                 | `List<T>`, `List<Any?>` when `items` is missing or empty |
| `object`                | `Map<String, Any?>`   |
| no `type` at all        | `Any?`                |

`format` is ignored on purpose: `JsonElement.extractedContent` narrows every JSON number to the
first of `Int`/`Long`/`Float`/`Double` that fits its *value*, so a `format: int64` attribute still
arrives as an `Int` whenever the value is small enough. Widening such a property to `Long` by hand
makes it fail on small values instead.

Nullability is read from OpenAPI 3.0 `nullable: true`, from an OpenAPI 3.1 type union
(`type: ["string", "null"]`), and from absence in `required`. Members marked `readOnly` become
`@Attr(mutable = false)`, so the generated resource exposes them without a setter.

A `string` member with an `enum` becomes a Kotlin `enum class` named after the resource and the
property (`status` on `LibraryEntry` → `LibraryEntryStatus`), emitted in the same file. Constants are
the wire values verbatim, because the processor round-trips them through `valueOf(...)` and
`Enum.name`; a `null` inside the `enum` list is dropped and handled as nullability instead. Values
that are not valid Kotlin identifiers fall back to `String` rather than being mangled. A value the
server adds later will fail `valueOf` at runtime — that is the cost of the typed mapping.

## Verification

```shell
./gradlew :openapi-generator:jvmKotest
```

`openapi-generator/src/test/resources` holds one fixture per dialect, and
`test/src/test/kotlin/it/maicol07/spraypaintkt_test/models/{openapi,apiplatform}` holds their
verbatim output so `:test:jvmKotest` keeps proving that generated schemas compile through KSP and
deserialize correctly. Regenerate them with:

```shell
./gradlew :openapi-generator:generateSchemas \
  -Pinput=openapi-generator/src/test/resources/jsonapi-books.yaml \
  -Ppackage=it.maicol07.spraypaintkt_test.models.openapi \
  -Poutput=test/src/test/kotlin
./gradlew :openapi-generator:generateSchemas \
  -Pinput=openapi-generator/src/test/resources/apiplatform-jsonapi.json \
  -Ppackage=it.maicol07.spraypaintkt_test.models.apiplatform \
  -Poutput=test/src/test/kotlin
```
