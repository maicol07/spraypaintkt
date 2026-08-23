# Project rules

## Summary

Spraypaint.kt is a Kotlin Multiplatform JSON:API client. KSP generates concrete resources from annotated schemas. `core` owns the model/query protocol, `annotation` exposes schema annotations, `processor` generates models, `ktor-integration` adapts Ktor, and `sample/composeApp` exercises Android, JVM, iOS, JS and Wasm.

## Stack

- Kotlin 2.3, KSP 2.3, kotlinx.serialization.
- Gradle 9.2 with Java 21 toolchains.
- Ktor 3 for the optional HTTP adapter.
- Compose Multiplatform / Material 3 for the sample.
- Kotest JVM tests.

## Conventions

- Common code must compile on JVM, Android, Native, JS and Wasm; do not use JVM-only APIs in `commonMain`.
- Schema types end in `Schema`; generated types remove that suffix.
- Use explicit `resourceType` and `endpoint` values for stable wire compatibility.
- Keep JSON:API relationship cardinality from the schema/runtime collection type. Never collapse a one-item to-many linkage.
- Public JSON/API input is untrusted. Validate shapes and throw specific serialization/protocol errors.
- Keep generated code simple. Add one regression test in `test` for every processor or wire-format bug.
- Tests are deterministic by default. Set `RUN_LIVE_TESTS=true` only for explicit integration runs against the demo API.
- Java/Kotlin bytecode targets are 21. Android min SDK is 26 and compile/target SDK is 36.
- Do not log request/response bodies by default.
- Do not commit generated `build` output, credentials or local SDK configuration.

## Verification

```shell
./gradlew :test:jvmKotest :processor:build :core:compileKotlinJs :core:compileKotlinWasmJs
./gradlew :sample:composeApp:compileKotlinJvm :sample:composeApp:compileKotlinJs :sample:composeApp:compileKotlinWasmJs
./gradlew :ktor-integration:assembleRelease
```

Inspect `ktor-integration/build/outputs/aar/*-release.aar!/AndroidManifest.xml` after Android manifest changes. Its declared `minSdkVersion` must remain at least 4; the project value is 26, which prevents legacy implicit permission injection when `targetSdkVersion` is absent from a library AAR.

## Known issues / TODO

- AGP 9 migration is pending. It requires `com.android.kotlin.multiplatform.library` for libraries and a separate Android application module for the Compose sample. Do this as one isolated migration, not piecemeal.
- JSON:API response wrappers use serializable wire DTOs in 3.0.0-rc1. Dynamic attributes and metadata use `Map<String, JsonElement>` and map to Kotlin values at the resource boundary.
- Duplicate `resourceType` registrations intentionally resolve to the first registered schema for compatibility. A future major version should reject ambiguity or require an explicit discriminator.
- Live tests mutate a third-party demo service and are not CI-safe. Replace them with a repository-owned local JSON:API test server before enabling them in CI.
- iOS framework embedding cannot be executed on Windows; verify it in Xcode/macOS CI.
- Snapshot publishing targets GitHub Packages only; Maven Central publishing is release-only.
- An injected Ktor `HttpClient` is caller-owned and is not closed by `KtorHttpClient.close()`.
- Dokkatoo 2.4 uses Dokka 1.9 and must run on JDK 21; JDK 25 fails inside Dokka's Java-version parser.
