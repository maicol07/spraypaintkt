# Changelog

<a name="3.0.0"></a>
## [3.0.0](https://github.com/maicol07/spraypaintkt/compare/2.4.0...3.0.0)

> Released on November 17, 2026

### ✨ Features

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/226ab12bbd2d8c50698786e59539988ea5ce6ec3"><code>226ab12</code></a> <b>http:</b> 🔧 add close method to Ktor client for resource management</summary>

  > - Introduced a `close` method to properly close the Ktor client instance.
  > - This change enhances resource management and prevents potential memory leaks.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/30d532789cd08c5fec6244644f07519c6a6bcca9"><code>30d5327</code></a> <b>network:</b> ✨ add NetworkResourceSchema and related models for API interaction</summary>

  > - Introduced NetworkResourceSchema interface for defining network resources.
  > - Added RecordingHttpClient for simulating HTTP responses in tests.
  > - Created RegressionConfig for API configuration with a stubbed base URL.
  > - Implemented RelationOptionsSchema for handling relationships in resources.
  > - Developed comprehensive regression tests to validate functionality.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/e121e9d5a8447e0c7ed80355286064ee21698e71"><code>e121e9d</code></a> <b>openapi-generator:</b> ✨ add OpenAPI schema generation support</summary>

  > - Introduced new schemas for OpenApiBook, OpenApiPerson, OpenApiReview, and LibraryEntry.
  > - Updated settings.gradle.kts to include the openapi-generator module.
  > - Enhanced README with instructions for generating schemas from OpenAPI documents.
  > - Added tests to validate the generated schemas against expected structures.

  </details>

### 🐛 Bug Fixes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/87a739047e752f54a1f946842eb7726e2c0431bc"><code>87a7390</code></a> <b>sample:</b> 🐛 handle JSON API exceptions during book refresh</summary>

  > - Added error handling for JSON API exceptions to display error messages to the user.
  > - Improved the refresh logic to clear the book list and handle loading states more effectively.

  </details>
- [`d28e158`](https://github.com/maicol07/spraypaintkt/commit/d28e158006411f5ccf995becfee3e73f90b7c6a0) **core:** 🐛 harden resource serialization and URL handling
- [`251f68e`](https://github.com/maicol07/spraypaintkt/commit/251f68e333a16220792b9710825b498424cbcf5d) **core:** 🐛 track dirty collection view mutations
- [`4b110b4`](https://github.com/maicol07/spraypaintkt/commit/4b110b4acacaf9186281ba36cd75153fbc1b6deb) **core:** 🐛 correct scope pagination and sort parameters
- [`2bc719a`](https://github.com/maicol07/spraypaintkt/commit/2bc719ade190984b35cb9566f3fb859c4babea78) **processor:** 🐛 generate correct schema metadata and enum values
- [`48a5dc5`](https://github.com/maicol07/spraypaintkt/commit/48a5dc5a3343464cd8079b0421653cbbb075c1c8) **core:** 🐛 harden JSON:API wire serialization
- [`d2b5d38`](https://github.com/maicol07/spraypaintkt/commit/d2b5d38d285b9daff6a18b53bfedda3db8fd25d6) **ktor:** 🐛 preserve injected client ownership
- [`798cab3`](https://github.com/maicol07/spraypaintkt/commit/798cab3a60305b5a4f219a7742aebf450a5ffaa5) **core:** 🐛 decode first queries as collections
- [`e527324`](https://github.com/maicol07/spraypaintkt/commit/e527324b852ffa3ff41e90cabf6ff204e9e6f0d7) **core:** 🐛 validate resources before deserialization
- [`8446249`](https://github.com/maicol07/spraypaintkt/commit/8446249ab6871992ebaab0ec65dd913c63356693) **core:** 🐛 clear explicit null relationships
- [`27e4919`](https://github.com/maicol07/spraypaintkt/commit/27e4919339f97b07e6d3d8d1e7ebc23ff67ade7a) **processor:** 🐛 handle nullable enum values
- [`7a61b5f`](https://github.com/maicol07/spraypaintkt/commit/7a61b5fc5f3d4397ed4708c7d03c20adeb5cfa70) **processor:** 🐛 track generated to-many relationship mutations
- [`207faa8`](https://github.com/maicol07/spraypaintkt/commit/207faa82b13017763aa00b86c32d2149b97505f5) **ktor:** 🐛 preserve caller-owned client lifecycle

### ⚡ Performance Improvements

- [`69c1400`](https://github.com/maicol07/spraypaintkt/commit/69c14003af3dd60e8afc0685f19781b2168076a5) **core:** ⚡ index resource registry lookups

### ♻️ Code Refactoring

- [`6ac3c92`](https://github.com/maicol07/spraypaintkt/commit/6ac3c922e4813e662b195dfe6677122eed712bb3) **build:** 🔧 remove unused JVM target configuration
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/b4c4463e67ec3c16ff21db380cb351c07bbcbf4d"><code>b4c4463</code></a> <b>pom:</b> ✨ update project name and description for clarity</summary>

  > - Changed the project name to "Spraypaint.Kt - KSP Processor" for better alignment with its functionality.
  > - Updated the description to "KSP resource generator for Spraypaint.Kt" to accurately reflect the purpose of the project.

  </details>
- [`ff28b52`](https://github.com/maicol07/spraypaintkt/commit/ff28b5256bc339958d9ed496824a21f99fa60809) **build:** 🔧 change Ktor client dependency from implementation to api
- [`a839c36`](https://github.com/maicol07/spraypaintkt/commit/a839c367c364206ec3fb699d252e55010af3b418) **build:** 🔧 change kotlinx.serialization.json dependency from implementation to api
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/73d962bd376998da1b57f32a7bc30e3b765c285e"><code>73d962b</code></a> <b>build:</b> 🔧 remove KSP plugin and clean up dependencies</summary>

  > - Eliminated the KSP plugin as it was no longer needed.
  > - Cleaned up commented-out code for better readability.

  </details>
- [`95d3fe9`](https://github.com/maicol07/spraypaintkt/commit/95d3fe9155088e3f7090c73acf5a8bb17007c20a) **exceptions:** 🔧 simplify error message for JSON:API requests
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/be2ca317ce3a3f9f319c8ad9d71fd62dd5f0866d"><code>be2ca31</code></a> <b>html:</b> 🔧 defer script loading for improved performance</summary>

  > - Added `defer` attribute to script tags for `skiko.js` and `composeApp.js`
  > - Enhances page load speed by allowing HTML parsing to continue while scripts are being fetched

  </details>
- [`8a13e94`](https://github.com/maicol07/spraypaintkt/commit/8a13e9430b5a114e046405004613a1a6a2b89f3d) **android:** 🔧 remove unnecessary activity attributes for cleaner manifest
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/3fa9f84ff2806836ba3b2b0e30f9b250d2fe97ec"><code>3fa9f84</code></a> <b>theme:</b> 🔧 simplify AppTheme composable structure</summary>

  > - Removed unnecessary state management for dark theme detection
  > - Streamlined the theme application logic for better readability

  </details>
- [`7e8535d`](https://github.com/maicol07/spraypaintkt/commit/7e8535da2efa2a865bd50c1f60525042e5238d2d) **api:** 🔧 change logging level to INFO for cleaner output
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/98f83090c4617df890cb28d47ca51ae5da612dea"><code>98f8309</code></a> <b>theme:</b> 🔧 invert status and navigation bar appearance logic</summary>

  > - Changed the logic to set light status and navigation bars based on the dark mode state.
  > - This improves the visual consistency of the app's theme across different modes.

  </details>
- [`6799638`](https://github.com/maicol07/spraypaintkt/commit/67996380d3ddf20f7d3725fed314debad8a605e2) **test:** 🔧 change logging level to INFO in BaseTest for cleaner test output
- [`2273380`](https://github.com/maicol07/spraypaintkt/commit/2273380876505f8ffeaec8e169ec2087129bfca7) **build:** 🔧 remove unused Kotlin reflection and serialization dependencies
- [`e514756`](https://github.com/maicol07/spraypaintkt/commit/e5147563de3c52a6741f33957f1f64cc6d4dc45d) **pluralize:** ♻️ fix missing newline at end of file
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/a49e396f4c19a76397f68d233118b669832580e2"><code>a49e396</code></a> <b>android:</b> 🔄 migrate to single-variant Android artifacts and AGP 9</summary>

  > - Updated all modules to use `com.android.kotlin.multiplatform.library` plugin.
  > - Consolidated `release` and `debug` Android artifact publications into a single variant.
  > - Separated Android-specific code into a dedicated `androidApp` module.
  > - Renamed and relocated AndroidManifest, resources, and entry points accordingly.
  > - Updated build scripts, dependencies, and documentation for consistency.

  </details>

### 📝 Docs changes

- [`a5222b1`](https://github.com/maicol07/spraypaintkt/commit/a5222b18eaa5472de55e1cd27d50917c09bb0f38) 📝 update pagination strategy in AppJsonApiConfig
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/c7272cb9f5aa283c7835f6caa80abeb6f0d5cb46"><code>c7272cb</code></a> 📝 update KSP plugin versions in README</summary>

  > - Updated KSP plugin versions to the latest stable releases for better compatibility and performance.
  > - Adjusted dependency references to use a consistent version variable.

  </details>
- [`da10af2`](https://github.com/maicol07/spraypaintkt/commit/da10af295a96421e045896e9454765abf95faf31) 📝 update section headers in README for better organization
- [`d823a88`](https://github.com/maicol07/spraypaintkt/commit/d823a886d18e6be37bcb8f83aa85f58b65696708) 📝 add testing and sample commands to README
- [`f9eb3d8`](https://github.com/maicol07/spraypaintkt/commit/f9eb3d8c13c430cb399d92c667cb31620cc31772) 📝 update README to clarify library modules and installation instructions
- [`6a7b1de`](https://github.com/maicol07/spraypaintkt/commit/6a7b1dedd71d64a7217e7c86cd5db78d904edef7) 📝 update license URLs to use HTTPS
- [`2a457f0`](https://github.com/maicol07/spraypaintkt/commit/2a457f0073ad3ac61b030e445937f7d842d055b2) 📝 document project architecture and verification
- [`5a2aa49`](https://github.com/maicol07/spraypaintkt/commit/5a2aa49952eb72a6fc4f0fd1b694163a367be95b) 📝 document 3.0 migration and project constraints

### ✅ Tests

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/93e5ea6abdbb5039aef9655aff7888665928dcd0"><code>93e5ea6</code></a> ✅ add support for conditional live tests using `RUN_LIVE_TESTS` env variable</summary>

  > - Introduced `liveTestsEnabled` flag to enable or disable live tests based on environment variables.
  > - Updated `BaseTest` and relevant test cases to respect live test conditions for better flexibility during execution.

  </details>
- [`a15c6cf`](https://github.com/maicol07/spraypaintkt/commit/a15c6cf9d56c3a9ac65e49fbfda3019df57af73b) **core:** ✅ assert exact resource JSON serialization

### 👷 Building scripts changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/d1dd88fc4b8103a29141233cec8156dd31cce7bd"><code>d1dd88f</code></a> <b>android:</b> 🔧 set minimum SDK version and update Java compatibility options</summary>

  > - Added minSdk version to ensure compatibility with newer features.
  > - Updated compileOptions to use Java 21 for improved performance and features.

  </details>
- [`bf0fdec`](https://github.com/maicol07/spraypaintkt/commit/bf0fdec4dfe3480e15b2ad8b7005b2837a03d58e) **android:** 🔧 set Java toolchain language version to 21
- [`b4da26c`](https://github.com/maicol07/spraypaintkt/commit/b4da26c1de3296ea6acd19f3dc10011f16185a7a) **android:** 🔧 set Java toolchain language version to 21
- [`3bca80c`](https://github.com/maicol07/spraypaintkt/commit/3bca80c218b7f3cbd4554603982b787a04ed3267) Build: 🔧 update shell script path for Xcode framework embedding
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/c616b2e402610087e414c43c74dfcfb2849b1224"><code>c616b2e</code></a> <b>sample:</b> 🔧 remove unused dependencies from build.gradle.kts</summary>

  > - Eliminated `compose.components.resources` and `kotlinx.serialization.json` as they are no longer needed.
  > - This helps to streamline the build process and reduce potential bloat in the project.

  </details>
- [`551b60b`](https://github.com/maicol07/spraypaintkt/commit/551b60b429e6ac7b6cf32af7db58ab68844a7fcc) 🔖 prepare 3.0.0-rc1
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/ef37b8c716d9f6c9bea32fc3c4a15fddb1686094"><code>ef37b8c</code></a> 🔖 prepare 3.0.0-rc2</summary>

  > Bump libVersion and document that the migration guide also applies when
  > moving from 3.0.0-rc1, whose Android coordinates this candidate breaks.

  </details>
- [`1660ffe`](https://github.com/maicol07/spraypaintkt/commit/1660ffe02a8e44d587234450e920bef58885b2e3) 🔖 prepare 3.0.0-rc3

### 👷 CI changes

- [`1957070`](https://github.com/maicol07/spraypaintkt/commit/19570704e810b13574761c9ff495bcd3604b430b) 🔧 remove node-version matrix from CI configuration
- [`dd186c9`](https://github.com/maicol07/spraypaintkt/commit/dd186c994580969c5a529089e7f3fdc73f56cfd6) 🔧 add read permission for contents in CI configuration
- [`6d865c9`](https://github.com/maicol07/spraypaintkt/commit/6d865c98c40ce071238f753006edfbdaf4ebcbfa) 🔧 add read permission for contents in CI configuration
- [`efc5905`](https://github.com/maicol07/spraypaintkt/commit/efc59053adb9480fa4af09ae78c46a17a12b64fb) Ci: 🔧 update permissions for CI test job to read contents
- [`01ef1b4`](https://github.com/maicol07/spraypaintkt/commit/01ef1b4223b3a9535dda02896ff5e58015726a1e) 🚦 streamline verification and publishing workflows
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/a9154e25c69214ab6398acdad9e9f474471a2f78"><code>a9154e2</code></a> 🚦 add push trigger for main branch in CI workflow</summary>

  > - This change ensures that tests are automatically run on pushes to the main branch, improving the CI process.

  </details>
- [`8138cc4`](https://github.com/maicol07/spraypaintkt/commit/8138cc4f39b5824bfa5c0f8c1f004631dffd5e1c) 👷 move the CI to the maicol07/actions reusable workflows
- [`2478fa7`](https://github.com/maicol07/spraypaintkt/commit/2478fa7fc1cd9d267a3d188a58f7862d789e6dd4) 👷 add Dependabot updates
- [`1887b07`](https://github.com/maicol07/spraypaintkt/commit/1887b074940039d8d556abc88d54163d8d83d2ef) 👷 move to maicol07/actions v2 and skip the checks on the release merge
- [`cbd8d83`](https://github.com/maicol07/spraypaintkt/commit/cbd8d830f82f0621653164e6eb81afdd32bfbac6) ⚡ publish the snapshot after the CI tests instead of running them again

### Other changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/f7c25e08729a16712fb114ae00be6c31216ef787"><code>f7c25e0</code></a> 🔧 Disable deployment validation for Maven Central</summary>

  > This commit disables the deployment validation step when publishing to Maven Central.
  >
  > The `validateDeployment` parameter in the `publishToMavenCentral()` function has been set to `false` across all modules to prevent potential validation-related publication failures.
  >
  > Specific changes:
  > - Updated `publishToMavenCentral()` to `publishToMavenCentral(validateDeployment = false)` in the following modules:
  >     - `core`
  >     - `annotation`
  >     - `ktor-integration`
  >     - `processor`

  </details>
- [`688cc7c`](https://github.com/maicol07/spraypaintkt/commit/688cc7c5db09c83fe0a92a87a3151c09afb989fe) 🔧 enable parallel sync for Gradle 9.4+
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/85a89ffa52071c07c3b908b96dfd4edfcdaeb954"><code>85a89ff</code></a> <b>deps:</b> 🔧 update Gradle and library versions</summary>

  > - Upgrade Gradle to 9.7.1 for improved performance and features.
  > - Update Android Gradle Plugin (AGP) to 9.3.2 for compatibility with new Gradle version.
  > - Bump Kotlin and related libraries to their latest versions for better support and functionality.
  > - Adjust compile SDK version to 37 to align with the latest Android development standards.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/c3dc33f5e750154b4c6be9dacaf47af4ef01b265"><code>c3dc33f</code></a> <b>deps:</b> 🔧 update library and plugin versions</summary>

  > - Update AGP to 9.4.1 and Kotlin to 2.4.20.
  > - Bump Ktor to 3.6.0, KSP to 2.3.12, and KotlinPoet KSP to 2.4.0.
  > - Update Swagger Parser, Logback Classic, and Kotest versions.
  > - Bump Compose, AndroidX Core, and UI Test sample dependencies.

  </details>

<a name="2.4.0"></a>
## [2.4.0](https://github.com/maicol07/spraypaintkt/compare/2.3.0...2.4.0)

> Released on January 04, 2026

### ✨ Features

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/1cce0b28027c571d37b5bd075c84261e0052f0f4"><code>1cce0b2</code></a> ✨ Enhance `JsonApiException` message</summary>

  > This commit enhances the `JsonApiException` to include the status code and body directly in the exception message.
  >
  > This provides more immediate and useful information when the exception is logged or printed, aiding in debugging efforts. The `JsonApiException`'s primary constructor is updated to pass a formatted string to the `RuntimeException` constructor.

  </details>

### 🐛 Bug Fixes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/3d16093f6b1495ce60cbcc55f94d5c2d37840ede"><code>3d16093</code></a> 🐛 Make Resource.Serializer public</summary>

  > This commit changes the visibility of the `Resource.Serializer` object from `private` to `public`.
  >
  > This allows external access to the serializer, which can be useful for custom serialization logic or other integrations.
  >
  > This also fixes a compilation warning about serializers visibility

  </details>

### ♻️ Code Refactoring

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/5f41f2f516cdd7823b9d137751ba1f1a5eeb2979"><code>5f41f2f</code></a> ♻️ Migrate to `com.android.kotlin.multiplatform.library` plugin</summary>

  > This commit migrates the project from the `com.android.library` plugin to the new `com.android.kotlin.multiplatform.library` plugin for improved integration with Kotlin Multiplatform.
  >
  > This change simplifies the Android configuration within the KMP `kotlin {}` block, removing the need for a separate `android {}` block and deprecated `androidTarget`.
  >
  > Specific changes include:
  > -   Updated `gradle/libs.versions.toml` to use the `com.android.kotlin.multiplatform.library` plugin ID.
  > -   Replaced `alias(libs.plugins.androidLibrary)` with `alias(libs.plugins.android.kmp.library)` in the `core`, `annotation`, and `ktor-integration` modules.
  > -   Replaced the deprecated `androidTarget {}` with the new `androidLibrary {}` block within the `kotlin {}` configuration.
  > -   Removed the now-redundant top-level `android {}` configuration blocks from all affected modules.
  > -   Removed explicit `jvmTarget` settings for Android, as they are now inferred correctly.

  </details>

### ⏪ Reverts

- [`247280e`](https://github.com/maicol07/spraypaintkt/commit/247280ed268d8435184c49286d82536a33f1a06e) Revert "refactor: ♻️ Migrate to `com.android.kotlin.multiplatform.library` plugin"

<a name="2.3.0"></a>
## [2.3.0](https://github.com/maicol07/spraypaintkt/compare/2.2.0...2.3.0)

> Released on December 31, 2025

### ✨ Features

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/a8a868635aa7e6a93f398da531d46fed71690c50"><code>a8a8686</code></a> ✨ Add `findOrNull`, `firstOrNull`, `last`, `lastOrNull`, and `exists` methods</summary>

  > This commit introduces several new methods to the `Scope` class to provide more flexible ways of querying resources, including non-throwing variants for finding records. It also adds an `exists` method for checking resource presence.
  >
  > Specific changes include:
  >
  > -   **`findOrNull(id)`**: Finds a resource by its ID, returning `null` instead of throwing an exception if not found.
  > -   **`firstOrNull()`**: Fetches the first resource in the collection, returning `null` if the collection is empty.
  > -   **`last()` and `lastOrNull()`**: Fetches the last resource by temporarily reversing the sort order.
  > -   **`exists()`**: Checks if any resource matching the query exists by efficiently fetching only one record.
  > -   The `find` and `first` methods have been updated to use their new `*OrNull` counterparts, throwing an exception if no record is found.
  > -   `RecordProxy` is now generic (`RecordProxy<R?>`) to support nullable data payloads from the `*OrNull` methods.
  > -   The KSP processor has been updated to correctly handle nullable type arguments (`R?`) when generating methods.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/e3f4754492ce2ff485b567b1b43cd0423c9938ab"><code>e3f4754</code></a> ✨ Propagate `JsonApiException` in `RecordProxy`</summary>

  > This commit enhances error handling by propagating `JsonApiException` through the `RecordProxy`. This allows for more robust error management when fetching records.
  >
  > Specific changes include:
  > - Added an optional `error` property of type `JsonApiException` to the `RecordProxy` data class.
  > - Updated the `findOrNull` method to wrap exceptions in a `RecordProxy` with the `error` field set.
  > - Modified the `find`, `first`, and `last` methods to check for and re-throw the `error` from the `RecordProxy`, ensuring exceptions are not suppressed.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/1662a738c72a9ea14583d54fe39bfc82a370addb"><code>1662a73</code></a> ✨ Preserve annotations on custom remote methods</summary>

  > This commit updates the `ResourceSchemaProcessor` to preserve annotations on custom remote methods defined within a `ResourceSchema`.
  >
  > Previously, any annotations on these methods (except for `@ScopeMethod`) were discarded during code generation. Now, all other annotations are correctly transferred to the corresponding methods in the generated remote class. This allows for the use of annotations like `@Deprecated` or custom annotations on these remote method definitions.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/208fb5edd6f731c9e5db4292037e1efb95a89920"><code>208fb5e</code></a> ✨ Add support for Enum serialization in setters</summary>

  > This commit enhances the attribute setters to correctly handle `Enum` types during serialization.
  >
  > When an attribute is an `Enum`, the generated setter will now serialize the `name` of the enum constant instead of the enum object itself. This change also correctly handles nullable enums.
  >
  > Specific changes:
  > - Updated `ResourceSchemaProcessor.kt` to check if a property is an `Enum`.
  > - If it is an enum, the generated setter now assigns `value.name` (or `value?.name` for nullable enums) to the attributes map.
  > - Added `enumSetterTest` in `SerializationTest.kt` to verify that both non-null and null enum values are serialized correctly.

  </details>

### ⚡ Performance Improvements

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/38c483d692c2000d9bbe6d452e76468f74cdac79"><code>38c483d</code></a> ⚡ Optimize `ResourceSchemaProcessor` performance</summary>

  > This commit refactors the `ResourceSchemaProcessor` to improve performance by reducing redundant lookups.
  >
  > Specific changes include:
  > - The `KSClassDeclaration` for the `Scope` class is now cached in a class property (`scopeClass`) during the initial `process` call.
  > - The `getDefaultConfig` function call is moved to happen only once per processing round, outside the main loop.
  > - The list of symbols with the `ResourceSchema` annotation is now collected into a list at the beginning of the `process` function.

  </details>

### ♻️ Code Refactoring

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/33cc818967f561ba19a65ae3ba6a3421b6eac699"><code>33cc818</code></a> ♻️ Simplify relationship serialization logic</summary>

  > This commit refactors the `toJsonApi` method in the `Resource` class to simplify how relationships are processed and serialized.
  >
  > The previous implementation unnecessarily wrapped single relationship objects in a list before processing.
  >
  > Specific changes:
  > - Removed an unused import for `kotlinx.serialization.encodeToString`.
  > - Simplified the handling of relationship values by using an Elvis operator (`as?/user

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/e61b63edd30d74ec91f2cf95d3e30051ff00d95e"><code>e61b63e</code></a> ♻️ Move extension functions into `Resource` class</summary>

  > This commit moves the `save()` and `destroy()` extension functions from the `it.maicol07.spraypaintkt.extensions` package directly into the `Resource` class.
  >
  > This change improves code organization and encapsulation by making these core actions instance methods of the `Resource` class itself.
  >
  > Specific changes:
  > - Deleted the `core/src/commonMain/kotlin/it/maicol07/spraypaintkt/extensions/Resource.kt` file.
  > - Added `save()` and `destroy()` methods to the `Resource` class, with their implementations moved from the deleted extension file.
  > - Updated the calls to `companion.urlForResource(this)` to use the now-available `toUrl()` instance method.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/061e0279a0713baf5a94e13ce5f9991b9f2443cb"><code>061e027</code></a> ♻️ Refactor error handling and simplify return types</summary>

  > This commit refactors error handling and simplifies method signatures across the codebase for improved consistency and clarity.
  >
  > The `save()` and `destroy()` methods in `Resource.kt` now return `Unit` and throw a `JsonApiException` on failure, instead of returning a `Boolean`.
  >
  > Similarly, the `find()`, `first()`, and `last()` methods in `Scope.kt` now throw a `NoSuchElementException` when a record is not found, instead of a generic `RuntimeException` or `JsonApiException`.
  >
  > Specific changes:
  > - Updated `Resource.save()` and `Resource.destroy()` to return `Unit` and throw `JsonApiException` on HTTP failure.
  > - Updated `Scope.find()`, `first()`, and `last()` to throw `NoSuchElementException` when no record is found.
  > - Added `@Throws` annotations to document the exceptions thrown by these methods.
  > - Performed general code cleanup, including removing unused imports, adding `@Suppress` annotations where needed, and improving code formatting for better readability.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/0ecc0903b43471a0fe4e6328f793978138108e8a"><code>0ecc090</code></a> ♻️ Adjust `save()` and `destroy()` return types to `Unit`</summary>

  > This commit changes the return type of the `save()` and `destroy()` methods from `Boolean` to `Unit`.
  >
  > This refactoring simplifies the methods, as they now throw a `JsonApiException` on failure instead of returning `false`. This makes the error handling more explicit and idiomatic.
  >
  > The corresponding tests have been updated to reflect this change, removing the assertions that checked for a `true` return value and now expecting `Unit`.

  </details>

### 📝 Docs changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/42bad09dd0a738bf11eb6ac6f612f32353d1ae4c"><code>42bad09</code></a> ✍️ Add `CancellationException` to `@Throws` annotations</summary>

  > This commit updates the KDoc for several suspend functions to explicitly declare that they can throw a `CancellationException`.
  >
  > This improves the correctness of the API documentation by making it clear to developers that these coroutine-based network operations are cancellable.
  >
  > Specific changes include adding `CancellationException` to the `@Throws` annotation for the following methods:
  >
  > - `Scope.find()`
  > - `Scope.first()`
  > - `Scope.last()`
  > - `Resource.save()`
  > - `Resource.destroy()`

  </details>

### Other changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/b5a2419bedd4811adf1ce725346023e96662d717"><code>b5a2419</code></a> ⬆️ Upgrade Gradle wrapper to 9.2.1</summary>

  > This commit updates the Gradle wrapper from version 8.14.3 to 9.2.1.

  </details>

<a name="2.2.0"></a>
## [2.2.0](https://github.com/maicol07/spraypaintkt/compare/2.1.0...2.2.0)

> Released on December 25, 2025

### ✨ Features

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/56f0dc1983633d161045404b3168280399880360"><code>56f0dc1</code></a> ✨ Add Material Icons Extended dependency</summary>

  > This commit adds the `compose.materialIconsExtended` dependency to the `composeApp` module. This provides access to the full set of Material Design icons for use within the Compose-based user interface.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/217aa2fb7c66764f83ff4a5659fa15439c7b4233"><code>217aa2f</code></a> ✨ Automatically infer `resourceType` and `endpoint` in `ResourceSchema`</summary>

  > This commit enhances the `ResourceSchema` annotation to automatically infer the `resourceType` and `endpoint` if they are not explicitly provided.
  >
  > Specific changes include:
  >
  > -   Updated the `ResourceSchema` annotation to allow `resourceType` and `endpoint` to be empty strings.
  > -   If `resourceType` is empty, it is now inferred from the lowercase class name (e.g., `UserSchema` becomes "user").
  > -   If `endpoint` is empty, it is now inferred from the pluralized lowercase class name (e.g., `UserSchema` becomes "users").
  > -   Updated the KDoc for the `ResourceSchema` annotation to reflect these changes.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9a947f9d3f8a1f04e4d9d5266b1ac2ce85434099"><code>9a947f9</code></a> ✨ Add constructor to generated resource classes</summary>

  > This commit introduces a primary constructor to the generated resource classes, allowing for the instantiation of models with initial attribute and relationship values. This simplifies object creation.
  >
  > Specific changes include:
  >
  > -   Refactored `ResourceSchemaProcessor` to extract attribute and relationship information into a `PropertyInfo` data class.
  > -   Added a `generateConstructor` function that creates a primary constructor for the generated resource class.
  > -   The constructor accepts all defined attributes and relationships as nullable optional parameters.
  > -   If a value is provided to the constructor, it is automatically added to the corresponding `attributes` or `relationships` map.
  > -   Added `ConstructorTest.kt` to verify the functionality of the new constructor, including tests for full, partial, and relationship-based instantiation.

  </details>

### 🐛 Bug Fixes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/dc4064632bbd3fef0b678c75eda64ac90745935b"><code>dc40646</code></a> 🐛 Workaround for KSP annotation value issue (google&#x2F;ksp#2356)</summary>

  > This commit implements a workaround for a KSP issue where annotation values cannot be retrieved directly.
  >
  > This is a workaround for google/ksp#2356.
  >
  > Specific changes include:
  >
  > - Added KSP extension functions (`findAnnotation`, `getArgumentValueByName`, `getAnnotationValue`) to manually retrieve annotation property values.
  > - Replaced direct annotation access (`getAnnotationsByType`) with the new `getAnnotationValue` extension function in `ResourceSchemaProcessor` for `@ResourceSchema`, `@Attr`, and `@Relation` annotations.

  </details>

### ♻️ Code Refactoring

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/72bc34c4f96ad9eede9515566a787c40ab810aef"><code>72bc34c</code></a> ♻️ Change test and sample API base URL</summary>

  > This commit updates the base URL for the API used in both the test suite and the sample application.
  >
  > The previous Render-hosted server is replaced with a new one hosted on PythonAnywhere for improved reliability and to remove the cold start warning.
  >
  > Specific changes:
  > - Updated the `baseUrl` in `BaseTest.kt` to `https://maicol07.eu.pythonanywhere.com/api`.
  > - Removed the `BeforeTest` setup that printed a wake-up warning for the old server.
  > - Updated the `baseUrl` in the sample app's `AppJsonApiConfig.kt` to the new URL.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/d65b5357df9e44271eec44f767226bd53507b15d"><code>d65b535</code></a> ♻️ Migrate test suite from kotlin.test to Kotest</summary>

  > This commit migrates the entire test suite from `kotlin.test` to the Kotest framework. This change provides a more expressive and structured way to write tests using behavior-driven and functional styles.
  >
  > Specific changes include:
  >
  > -   Replaced `kotlin-test` dependency with `kotest-assertions-core` and `kotest-framework-engine`.
  > -   Updated all test classes (`ConstructorTest`, `ErrorsTest`, `QueryTest`, `SerializationTest`, `WritingTest`) to use Kotest's `FunSpec` and its assertion syntax (e.g., `shouldBe`, `shouldThrow`).
  > -   Removed `@Test` annotations and `runTest` blocks in favor of Kotest's `test` blocks.
  > -   Removed the unused `BaseTest` class, as it was only providing a `@BeforeTest` hook that is no longer needed.
  > -   Updated the `test/build.gradle.kts` file to include the Kotest dependencies and plugin.
  > -   Adjusted the GitHub Actions workflow in `test.yml` to execute the tests using the `jvmKotest` Gradle task instead of `test`.

  </details>

### Other changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/1ce761a269422fc66b292f27c84e6d06bb9c5022"><code>1ce761a</code></a> <b>deps:</b> ⬆️ Update dependencies</summary>

  > This commit updates the following dependencies to their latest versions:
  >
  > - Android Gradle Plugin (AGP) to 8.13.2
  > - logback-classic to 1.5.22

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/a5742bd8da6d2b15022978b536727b2672b76255"><code>a5742bd</code></a> <b>deps:</b> ⬆️ Update dependencies</summary>

  > This commit updates the following dependencies to their latest versions:
  >
  > - Kotlin to `2.3.0`
  > - KSP to `2.3.4`
  > - logback-classic to `1.5.23`
  > - androidx-activityCompose to `1.12.2`

  </details>

<a name="2.1.0"></a>
## [2.1.0](https://github.com/maicol07/spraypaintkt/compare/2.0.0...2.1.0)

> Released on December 04, 2025

### ✨ Features

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/23096e5728b737d16425e11c04a08a2a2040f737"><code>23096e5</code></a> ✨ Publish artifacts to both Maven Central and GitHub Packages</summary>

  > This commit updates the CI/CD workflows to publish release and snapshot artifacts to both Maven Central and GitHub Packages.
  >
  > Specific changes include:
  >
  > - **`release.yml`**: Added a new step to publish release artifacts to GitHub Packages in addition to Maven Central.
  > - **`snapshot.yml`**: Added a new step to publish snapshot artifacts to Maven Central in addition to GitHub Packages.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/852b68ef434faa201b0c52d6f042d711934b2e79"><code>852b68e</code></a> ✨ Add support for Enum attributes</summary>

  > This commit adds support for serializing and deserializing Enum attributes in resource models.
  >
  > The annotation processor now correctly identifies Enum properties and generates the appropriate code to convert them to and from strings during serialization and deserialization.
  >
  > Specific changes include:
  > - Added logic in `ResourceSchemaProcessor` to check if an attribute property is an `ENUM_CLASS`.
  > - Updated the getter for attributes to use `Enum.valueOf()` for deserialization.
  > - Added a new `BookGenre` enum to the test models.
  > - Added `enumSerializationTest` and `enumDeserializationTest` to verify the new functionality.

  </details>

### ♻️ Code Refactoring

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/99a90dc9327d4f04ee390ec81c3825a35a10497f"><code>99a90dc</code></a> ✨ Use KSP for reflection in processor</summary>

  > This commit refactors the `ResourceSchemaProcessor` to exclusively use KSP (Kotlin Symbol Processing) APIs for symbol processing, removing the dependency on `kotlin-reflect`. This improves performance and aligns the processor more closely with KSP's intended usage.
  >
  > Specific changes include:
  > - Replaced `kotlin.reflect.*` imports with KSP equivalents like `getClassDeclarationByName` and `getDeclaredFunctions`.
  > - Implemented `generateBaseResourceProperties` and `generateScopeFunctions` using KSP APIs to inspect `Resource` and `Scope` classes.
  > - Removed the usage of `kotlin.reflect` for analyzing properties and functions, such as checking modifiers, types, and annotations.
  > - A `Resolver` instance is now stored as a property in the processor for broader access.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/d19660a642547abb84289dd0040379d7093924e9"><code>d19660a</code></a> <b>processor:</b> 🎨 Improve code style and logic</summary>

  > This commit introduces several code style improvements and minor refactorings to the `ResourceSchemaProcessor`.
  >
  > Specific changes include:
  > - Renamed `BaseResourceProperties` to `baseResourceProperties` and `BaseResourcePropertiesWithDelegate` to `baseResourcePropertiesWithDelegate` to adhere to Kotlin's naming conventions for private properties.
  > - Removed a redundant `continue` statement.
  > - Simplified a boolean comparison in the relationship validation logic.

  </details>
- [`80946e4`](https://github.com/maicol07/spraypaintkt/commit/80946e4ef0cb840f741fb95d61dcefcc743c05c0) 🎨 Code refactor

### Other changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/455099aa24a613eb2f0ff5fa8232788fbb2ca169"><code>455099a</code></a> 🗑️ Remove explicit JVM toolchain configuration</summary>

  > This commit removes the explicit JVM toolchain settings from the `processor` and `sample/composeApp` modules. The toolchain version is managed centrally in the root build configuration, making these specific declarations redundant.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/813a03c6271c10a863e8768d7265edb4c2c58135"><code>813a03c</code></a> 🗑️ Remove unused Gradle properties</summary>

  > This commit removes an obsolete Gradle property.
  >
  > The `kotlin.mpp.androidGradlePluginCompatibility.nowarn=true` property is no longer needed and has been removed to clean up the configuration.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/daff8311b5c2b4f3745b1bbbc34a8273ab1d88d8"><code>daff831</code></a> <b>deps:</b> ⬆️ Update dependencies and Gradle wrapper</summary>

  > This commit updates various dependencies to their latest versions and upgrades the Gradle wrapper.
  >
  > Specific changes include:
  >
  > - Gradle Wrapper updated to 8.13.
  > - Android Gradle Plugin (AGP) updated to 8.13.1.
  > - Kotlin updated to 2.2.21.
  > - KSP updated to 2.3.2.
  > - Ktor updated to 3.3.3.
  > - Other libraries like KotlinPoet, Coroutines, Serialization, Maven Publish, and Dokka have been updated.
  > - Android `compileSdk` and `targetSdk` updated to 36.
  > - The KSP workaround has been updated to use `KspAATask` instead of `KotlinCompile`.
  > - Removed explicit `SonatypeHost.CENTRAL_PORTAL` from Maven publishing configuration.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/ec5d17d89694b99ee184a4a15485466488e4b36b"><code>ec5d17d</code></a> <b>deps:</b> ⬆️ Update dependencies and plugins</summary>

  > This commit updates Gradle, KSP, and adds the Dokka Javadoc plugin.
  >
  > Specific changes:
  >
  > -   Updated Gradle Wrapper from 8.13 to 8.14.3.
  > -   Updated the KSP version from 2.3.2 to 2.3.3.
  > -   Added the `org.jetbrains.dokka-javadoc` plugin.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/0b98394afb2393e879655a2fc01e39ba64665ac5"><code>0b98394</code></a> <b>build:</b> 🎨 Clean up suppress annotations and file paths</summary>

  > This commit introduces minor code cleanup and build-related adjustments.
  >
  > Specific changes include:
  >
  > - Removed the `@file:Suppress("MissingTestClass")` annotation from `MutableList.kt` and `KtorHttpClient.kt`.
  > - Added the `@Suppress("unused")` annotation to the `DirtyList` class.
  > - Renamed the Android launcher icon directory from `mipmap-anydpi-v26` to `mipmap-anydpi`.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/c87583771e5c4de2ff73ffa234c9f69cb64cf2c9"><code>c875837</code></a> <b>deps:</b> ⬆️ Migrate from Dokka to Dokkatoo for documentation</summary>

  > This commit migrates the project's documentation generation from the deprecated Dokka Gradle plugin to the Dokkatoo plugin.
  >
  > Specific changes include:
  > - Replaced the `org.jetbrains.dokka` plugin with `dev.adamko.dokkatoo-html`.
  > - Updated the `libs.versions.toml` file to use Dokkatoo version 2.4.0.
  > - Updated the root `build.gradle.kts` to use the new Dokkatoo configuration syntax.
  > - Removed the old Dokka multimodule configuration and tasks.
  > - Updated the documentation build workflow (`.github/workflows/docs.yml`) to use the `:dokkatooGenerate` task and the new output directory.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/f86bd4006012de765d1812ebce903b7652435121"><code>f86bd40</code></a> <b>deps:</b> ⬆️ Update dependencies</summary>

  > This commit updates the following dependencies to their latest versions:
  >
  > - `androidx-activityCompose` to `1.12.1`
  > - `androidx-uiTest` to `1.10.0`

  </details>

<a name="2.0.0"></a>
## [2.0.0](https://github.com/maicol07/spraypaintkt/compare/1.0.0...2.0.0)

> Released on December 31, 2024

### ✨ Features

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9cef7301c2b72dcc833496ed60e77d125a521487"><code>9cef730</code></a> ✨ Add annotation and processor modules</summary>

  > This commit introduces two new modules:
  >
  > - **annotation:** Contains the `@Model` annotation for marking classes to
  >  be processed.
  > - **processor:** Contains the KSP processor that generates model representations of annotated classes.
  >
  > This lays the foundation for generating model classes from DTOs, enabling a more convenient way to interact with Spraypaint data.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/f1d03db9901349d6a1b9a19c2a604c2c6386745a"><code>f1d03db</code></a> ✨ Initial implementation of ResourceSchema annotation processor</summary>

  > This commit introduces a new annotation processor for the `ResourceSchema` annotation, which generates model representations of annotated
  >  classes.
  >
  > The processor generates:
  > - Resource classes that extend the `Resource` class and implement the annotated interface.
  > - Companion objects for each resource class, providing metadata such as resource type, endpoint, and configuration.
  > - Properties for attributes, to-one relationships, and to-many relationships defined in the
  >  annotated interface.
  > - Scope functions for each resource class, allowing for convenient configuration of resource options.
  >
  > This change enables developers to define JSON:API resource schemas using annotations, simplifying the process of creating and managing resource models.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/0ebf836873ee7be8da0cdc62c3f582767ff24608"><code>0ebf836</code></a> ✨ Replaced ModelGenerator with Resource Registry</summary>

  > This commit introduces a Resource Registry to manage and create instances of resources.
  >
  > The ResourceRegistry provides the following functionalities:
  >
  > -
  >  Registering resources with their companion objects.
  > - Creating instances of registered resources by class or type.
  > - Retrieving resource companions by class or type.
  > - Getting the KClass of a resource from its companion.
  > - Retrieving the entry (KClass and companion) for a resource by type.

  </details>
- [`6b224fa`](https://github.com/maicol07/spraypaintkt/commit/6b224fa86127e38ac9f72d945d20a840e3be8c6d) ✨ Replaced Client with generated resources classes
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/69ebd213a81523476ff2fef638b3836abec3bb11"><code>69ebd21</code></a> ✨ Add support for JS, Wasm, Linux, and Windows targets</summary>

  > This commit adds support for building and running the library on JavaScript, WebAssembly
  > , Linux, and Windows platforms.
  >
  > The changes include:
  > - Adding necessary dependencies and configurations for each platform.
  > - Updating build scripts to include new targets.
  > - Ensuring compatibility and proper functionality on the newly supported platforms.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/4c8a7276d84949756afcef6c550b5c7d9bad9ca8"><code>4c8a727</code></a> ✨ Add attribute name customization</summary>

  > This commit introduces the ability to customize the name of attributes in JSON:API resources.
  >
  > The `@Attr` annotation
  >  now accepts an optional `name` parameter to specify the desired attribute name. If not provided, the attribute name will be automatically derived from the property name, optionally transformed to snake case using the `autoTransform` parameter (enabled by default).
  >
  > This enhancement provides greater flexibility in mapping Kotlin properties to JSON:API attributes,
  >  allowing for more control over the generated API structure.

  </details>
- [`7b4bffc`](https://github.com/maicol07/spraypaintkt/commit/7b4bffc9d4f1dc81a2fe473fbb7d30eaba3c1f82) ✨ Optimized file dependency in processor
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/7e1b1faeec2609b5fb066d261c36a2d42198799d"><code>7e1b1fa</code></a> ✨ Add Resource serialization support</summary>

  > This commit introduces serialization support for Resource objects, allowing them to be easily converted to and from JSON:API format.
  >
  > Specific changes include:
  >
  > - Added a new `ResourceSerializer` object to handle serialization and deserialization.
  > - Annotated the `Resource` class with `@Serializable` to enable serialization.
  > - Updated the `toJsonApiString()` method to include "included" data.
  > - Added `fromJsonApi()` methods to
  >  the `Resource` and `Resource.CompanionObj` interfaces for deserialization.
  > - Modified the `save()` extension function to handle deserialization of newly created resources.
  > - Updated the `Scope` class to use `fromJsonApiString()` for deserializing responses.
  > - Refactored the `Deserializer` class to support deserialization of both single and collection responses.
  > - Updated the `ResourceRegistry` to use `Resource.CompanionObj` instead of `Resource.Companion`.
  > - Added necessary dependencies for serialization in the `build.gradle.kts` files.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/b4509240923620e9e0c4ebe6fff910fa817ca309"><code>b450924</code></a> ✨ Added JS and Wasm targets to Ktor integration</summary>

  > This commit adds Ktor integration for JS and Wasm targets, including:
  >
  > - Support for browser, nodejs, and D8 environments in Wasm.
  > - Support for browser and nodejs environments in JS.
  > - Updates to dependencies and build configurations for the new targets.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/23a9bf537c8cbf85b6fadd9d955787c8cbab862c"><code>23a9bf5</code></a> ✨ Initial compose app sample</summary>

  > This commit introduces a basic Compose Multiplatform application sample that demonstrates the usage of the library.
  >
  > The sample includes:
  > - A simple UI to display a list of books fetched from a remote API.
  > - Support for Android, iOS, Desktop, and Web (JS and Wasm).
  > - Basic theme and styling.
  > - Data models for Book, Publisher, Author, and Review.
  > - An example of fetching data using the library's API.
  > - A simple detail view for displaying book information.
  > - A floating action button to refresh the book list.
  > - Dark and light theme support.
  > - Basic icons and fonts.
  >
  > This sample serves as a starting point for building more complex applications using the library.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9ca7afd5cc888b8f80af4687a5ca2151c0febb5b"><code>9ca7afd</code></a> ✨ Support for abstract classes as ResourceSchema</summary>

  > This commit introduces support for abstract classes as ResourceSchema,
  >  providing more flexibility in defining resource schemas.
  >
  > Specific changes include:
  >
  > - Updated ResourceSchemaProcessor to support abstract classes as ResourceSchema.
  > - Updated Resource to be an interface instead of an abstract class.
  > - Updated Resource to use delegated properties for some properties.
  > - Updated tests and samples to reflect the changes.
  > - Updated documentation to reflect the changes.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/8b697de3540c246d99ee44afeb51a925c63f6533"><code>8b697de</code></a> ✨ Reworked relationships</summary>

  > This commit refactors the way relationships are handled in SpraypaintKt.
  >
  > The previous `ToOneRelationship` and `ToManyRelationship` annotations have been replaced with a single `Relation` annotation.
  >  This simplifies the API and allows for more flexibility in defining relationships.
  >
  > Instead of defining relationships on the schema class using the annotations, they are now defined as properties in the schema interface using the `Relation` annotation.
  >  This makes it easier to define custom relationships with different types and behavior.
  >
  > Relationships are now automatically fetched when accessed.
  >  A caching mechanism has been added to prevent unnecessary network requests.
  >
  > Additional changes include:
  >
  > - Removed `ToOneRelationship` and `ToManyRelationship` annotations
  > - Added `Relation` annotation to handle to-one and to-many relationships
  > - Refactored `ResourceSchemaProcessor` to generate relationships as properties
  > - Removed unused classes and extensions related to previous relationship handling
  > - Added logic for caching and automatically fetching relationships
  > - Updated README, sample and test code to reflect the new changes

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/2f9773ddee1734b78ff9ef61cc08163fc01191d2"><code>2f9773d</code></a> ✨ Make attributes mutable by default</summary>

  > This commit introduces the ability to specify whether an attribute is mutable using the `@Attr` annotation.
  > By default, all attributes are now mutable.
  > The `mutable` property in the `@Attr` annotation can be used to override this behavior and make an attribute immutable.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/88d893eee08ae4037d7ce98b76d8c35464d7712c"><code>88d893e</code></a> ✨ Optimized file dependency in processor</summary>

  > This commit optimizes file dependency management in the annotation processor.
  >
  > The changes include:
  >
  > - File dependency is now calculated automatically based on originating files of generated code.
  > - Resource schema class name validation is added.
  > - Resource schema class must be abstract is checked.
  > - Bug fix: JsonApiConfig validation is fixed.
  > - Bug fix: ResourceSerializer annotation is generated correctly.
  > - All errors and warnings are reported to KSPLogger.
  > - Bug fix: Relationships must be val is checked and reported to KSPLogger.
  > - Bug fix: Relationship type must be ResourceSchema is checked and reported to KSPLogger.
  > - Bug fix: Relation ResourceSchema not found is reported to KSPLogger.

  </details>

### 🐛 Bug Fixes

- [`37cafd1`](https://github.com/maicol07/spraypaintkt/commit/37cafd10a70d9eda1f2f853c15613d9ca18ac478) 🐛 Sync not working
- [`cdc1a5d`](https://github.com/maicol07/spraypaintkt/commit/cdc1a5da159e75a29aac8a2b71db4be3097daf8d) Change status code type in error object as per JSON:API specs
- [`7970dab`](https://github.com/maicol07/spraypaintkt/commit/7970dab60638f8838263e441ee920a11f700b108) Don't catch automatically error exception
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/1871a1935c19febf9a6b1167354f71f355618f97"><code>1871a19</code></a> Resource destruction method requiring resource</summary>

  > The `destroy` method for resources now correctly takes no arguments and returns a boolean indicating success or failure. This simplifies the API and makes
  >  it more consistent with other methods.

  </details>
- [`be91e4e`](https://github.com/maicol07/spraypaintkt/commit/be91e4e656b60d2132acb25baf3bc95e89e152a7) Changed deprecated URL
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/4e9258a12443eae0f4bc47cac99fcdede1104aa7"><code>4e9258a</code></a> 🐛 Validate that resource schema is an interface or abstract class</summary>

  > This commit adds a validation step to ensure that the resource schema class is either an interface or an abstract class.
  >
  > Previously, the processor did not enforce this restriction, which could lead to unexpected behavior. This change ensures that the resource schema class is properly defined and prevents potential errors during processing.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/8441f55ad0d99fc089f87346556e258aa15d70c2"><code>8441f55</code></a> 🐛 Fix relationship type erasure</summary>

  > This commit fixes an issue where the type of a relationship was being erased, causing unexpected behavior.
  >
  > Specifically, the `get()` and `set()` methods for relationships now correctly handle type parameters and ensure that the relationship's type is preserved.
  >
  > Additionally, the default value for a relationship is now determined based on whether the property is abstract or not, and whether the relationship is to-many or to-one. This ensures that the correct default value is used, even if the relationship is null.

  </details>
- [`3d33f8a`](https://github.com/maicol07/spraypaintkt/commit/3d33f8a7ed8ab5e3358caaa8c02a7cca44d76a9e) 🐛 Fix nullable relationship not being nullable anymore when generating model

### ♻️ Code Refactoring

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/aea497449f440d060bd4f1740f2e41eb8267e8eb"><code>aea4974</code></a> ♻️ Move HTTP interfaces to interfaces package</summary>

  > Moved `HttpClient`, `HttpClientResponse`, and `JsonApiConfig` interfaces to the `interfaces
  > ` package to better organize the codebase.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/aa8fdbcd3f12b43d9d8ee345745e7a78b8d627cb"><code>aa8fdbc</code></a> 🧹 Code cleanup</summary>

  > Removed unused imports and suppressed warnings.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/5c20e3ca84b5b3609874d2dc702e593b813b2291"><code>5c20e3c</code></a> ✨ Improved relationships management</summary>

  > This commit refactors the way relationships are handled in ResourceSchemaProcessor to make the code more robust and reusable:
  >
  > - Introduced `RelationshipPropertyData` data class to store information about each relationship property: delegate format, parameters, and type.
  > - Created `generateRelationships` function to handle the generation of relationship properties, taking care of common logic for to-one and to-many relationships.
  > - Reimplemented `generateToOneRelationships` and `generateToManyRelationships` using `generateRelationships`, reducing code duplication and improving maintainability.
  > - Replaced `canBeEmpty` with `canBeNull` for consistency with other parts of the code.
  > - Improved the determination of default values for relationships using a dedicated getter.
  > - Updated the `config` property initialization to gracefully handle cases where the config is not specified in the annotation.
  >
  > Fixes the "not matching predicate" error

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9205f1832330e72837cbfd80c2705425c7d546ba"><code>9205f18</code></a> ⚡️ Improve KtorHttpClient instantiation</summary>

  > This commit refactors the `KtorHttpClient` class to provide more flexibility in its instantiation:
  >
  > - The constructor now accepts an optional `HttpClientEngineFactory` parameter, allowing users to specify the desired engine for the HTTP client.
  > - The `httpClientOptions` parameter is still available to customize the client's configuration.
  > - A new `httpClient` parameter is introduced, allowing users to provide a pre-configured `HttpClient` instance. If this parameter is provided, the `engineFactory` and `httpClientOptions` parameters are ignored.
  >
  > This change improves the usability of the `KtorHttpClient` class by enabling users to choose the most appropriate engine and configuration for their needs. It also removes the dependency on the `Logging` plugin, simplifying the default configuration and improving performance.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/870dd98fa4df759047d5da0fceba5921d17b5d39"><code>870dd98</code></a> ✨ Rename `canBeEmpty` to `canBeNull` in `ToOneRelationship` annotation</summary>

  > This commit renames the `canBeEmpty` parameter to `canBeNull` in the `ToOneRelationship` annotation, improving clarity and consistency with the concept of nullable relationships.
  >
  > This change reflects the fact that the parameter indicates whether the relationship can have a null value, not whether it can be empty.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/2be87bde50202a678be3811f69b840b61325e3e8"><code>2be87bd</code></a> 🧹 Refactor attribute names to camel case</summary>

  > This commit refactors attribute names in the data models to use camel case instead of snake case.
  >
  > Specific changes include:
  >
  > - Renamed `book_id` to `bookId` in `ReviewSchema`.
  > - Renamed `reader_id` to `readerId` in `ReviewSchema`.
  > - Renamed `publisher_id` to `publisherId` in `BookSchema`.
  > - Renamed `custom_field` to `customField` in `PublisherSchema`.
  >
  > This change improves the consistency and readability of the codebase by adhering to the common Kotlin coding style for property names.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/19cdf20e6e76498f5a4dd53678840b3ab0e6d15c"><code>19cdf20</code></a> 🧹 Refactor KtorHttpClient to use single sendRequest function</summary>

  > This commit refactors the `KtorHttpClient` class to reduce code duplication by introducing a single `sendRequest` function.
  >
  > This function handles sending requests for all HTTP methods (GET, PATCH, POST, PUT, DELETE), consolidating the logic for setting parameters, accepting JSON:API content type, and handling responses.
  >
  > This refactoring improves code maintainability and readability.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/3ec9fdbb506b1c3ff9cc021749804d911c72e377"><code>3ec9fdb</code></a> 🧹 Refactor relationship generation in ResourceSchemaProcessor</summary>

  > This commit refactors the relationship generation logic in the `ResourceSchemaProcessor` to streamline the process of creating to-one and to-many relationships for resources.
  >
  > Instead of having separate functions for generating to-one (`generateToOneRelationships`) and to-many (`generateToManyRelationships`) relationships, this commit introduces a single function `generateRelationships` that handles both types of relationships. This consolidation improves code readability and maintainability by reducing redundancy.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/26496fff9e375903a8d07b4bff7a13d9beb8158c"><code>26496ff</code></a> 🧹 Minor code improvements in ResourceSchemaProcessor</summary>

  > - Improved error message when a Schema class does not end with "Schema".
  > - Fixed a potential type mismatch issue when generating code for MutableMap properties.
  > - Updated relationship initialization to improve readability.
  > - Added an empty line for better readability in attribute generation.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/73a81f9b449033f545ca365367d0496ea2eca9c1"><code>73a81f9</code></a> 🧹 Moved Book model to sample data models</summary>

  > This commit moves the `Book` model from `it.maicol07.spraypaintkt_test.models` to `it.maicol07.spraypaintkt.sample.data.models`.
  >
  > This change improves the organization of the sample application by grouping data models under a dedicated package.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/66ef97f74ce4e2b0f559e958f9e8f9ed06022130"><code>66ef97f</code></a> ✨ Optimize attribute delegation</summary>

  > This commit optimizes the attribute delegation mechanism in Resource classes:
  >
  > - **Direct attribute access:** Instead of relying on delegate properties (`attribute` and `nullableAttribute`), attributes are now accessed and modified directly using the `attributes` map.
  > - **Improved performance:** By eliminating delegate properties, the overhead associated with property delegation is removed, resulting in improved performance.
  > - **Reduced code complexity:** The code for attribute handling is simplified and more concise, improving readability and maintainability.
  > - **Enhanced type safety:** Type casts are performed explicitly during attribute access, ensuring type safety and preventing potential runtime errors.
  > - **Simplified attribute generation:** The code generation logic for attribute properties is simplified, making the processor more efficient.
  >
  > These changes optimize the attribute handling in Resource classes, improving performance and reducing code complexity.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/585a37d0d5bfafce501f76702fdfc2c453285742"><code>585a37d</code></a> ♻️ Update BookSchema.reviews type</summary>

  > This commit updates the type of the `reviews` property in `BookSchema` from `MutableList<out ReviewSchema>` to `List<ReviewSchema>`.
  >
  > This change removes the mutability of the `reviews` property, aligning it with the intended usage as a read-only collection of reviews associated with a book.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/647704b7b602d3f92a6d143715b1e941690f37be"><code>647704b</code></a> 🧹 Optimized file dependency in processor</summary>

  > Optimized the file dependency in the processor by removing unused data class and code.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/f33e1a468a58530f6422bfb00320764a57a7ad19"><code>f33e1a4</code></a> 🧹 Remove unused imports from App.kt</summary>

  > This commit removes unused imports from the `App.kt` file, cleaning up the codebase.
  >
  > Specific changes include:
  >
  > - Removed imports for `CircularProgressIndicator`, `LazyHorizontalGrid`, `Star`, `pulltorefresh.rememberPullToRefreshState`.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/1e0a780846fbe77a80a7721495240dd8ab360cf8"><code>1e0a780</code></a> ✨ Make ResourceSerializer generic</summary>

  > This commit refactors the `ResourceSerializer` to be generic, allowing for more flexibility in serialization and deserialization of resources.
  >
  > Specific changes include:
  >
  > - Made `ResourceSerializer` an abstract class with a type parameter `R` extending `Resource`.
  > - Added a private object `Serializer` within `Resource` to handle serialization.
  > - Updated `serialize()` and `deserialize()` methods to use the generic type `R`.
  > - Modified the `deserialize()` method to handle both single and collection responses, and to ensure the deserialized resource is of type `R`.

  </details>

### 📝 Docs changes

- [`4985691`](https://github.com/maicol07/spraypaintkt/commit/498569161617d1a48a11e11f6e29963b64551d76) 📝 Updated docs
- [`c84d275`](https://github.com/maicol07/spraypaintkt/commit/c84d2759991dcf125913fa1d832a85d4cdeaf826) 📝 Updated documentation for 2.0.0
- [`5bc1ff8`](https://github.com/maicol07/spraypaintkt/commit/5bc1ff8e3fcaa512ceb110c0c18757a4e1a47203) Updated docs
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9629c14eabce1da54b966f5a83bba68bea0663dd"><code>9629c14</code></a> 📝 Update README.md with info about abstract class relations</summary>

  > Added a warning and solution to the README about using relations with abstract classes.
  >
  > When using an abstract class for a relation property, it must be declared `open` and have a default value. Otherwise, a Kotlin inheritance error will be thrown. This is now documented in the README with a warning and a tip.

  </details>

### ✅ Tests

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/74b8510bfc255111230793e8dfe72c7852a13f64"><code>74b8510</code></a> ✅ Added ExtendedBookSchema</summary>

  > This commit introduces the ExtendedBookSchema for testing purposes. It includes additional relationship fields for book entities.
  >
  > - Added ExtendedBookSchema interface with attributes and relationships
  > - Added BaseBookSchema for code sharing
  > - Created a test case for ExtendedBook queries

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/e202694bb9e66a2b0ec35997ea61270ad24c2fbd"><code>e202694</code></a> 🧹 Removed publisher_id assertion in QueryTest</summary>

  > Removed the assertion for the `publisher_id` property in the `QueryTest` class.
  > This change removes a redundant check that is no longer relevant.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/cd485d51736e6817e160961be71fb8b2aebfdaca"><code>cd485d5</code></a> 🧪 Removed unnecessary assertion in extended books test</summary>

  > This commit removes an unnecessary assertion that checked the size of the extended books list.
  >
  > This assertion was redundant as the test focuses on verifying the type and structure of the data.

  </details>
- [`8e66f6b`](https://github.com/maicol07/spraypaintkt/commit/8e66f6bbca84f277fe472adb8e49613fa3aafd67) ✅ Added relationships writing test
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/6e3acd6aefd96567ee947e141d40991916c438c6"><code>6e3acd6</code></a> 🙈 Ignore relationship tests</summary>

  > This commit ignores the relationship tests for Reviews, as they are currently bugged.
  >
  > The tests that check the relationship between Reviews, Books, and Readers are now ignored. This is due to an ongoing bug that prevents the relationships from being properly loaded.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/0017b8ba744cbd2bac61437bf1e526efcc8e172c"><code>0017b8b</code></a> 🐛 Disabled some tests due to DEMO server bug</summary>

  > Disabled some tests that were failing due to a bug in the DEMO server.
  > This prevents the CI from failing.

  </details>

### 👷 CI changes

- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/42fcd05f6af6b837c9716465003a29e13035aba4"><code>42fcd05</code></a> Run unit tests in test workflow</summary>

  > The commit updates the test workflow to specifically run unit tests using `./gradlew test:test`.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/545ef0511cdbd8cea98bd4f840c770429319fd8d"><code>545ef05</code></a> ⬇️ Downgrade JDK to 17</summary>

  > This commit downgrades the JDK version used in the GitHub workflows from
  > 21 to 17.
  > This change affects the following workflows:
  >
  > - Snapshot
  > - Docs
  > - Test
  > - Release

  </details>
- [`7810302`](https://github.com/maicol07/spraypaintkt/commit/781030298a4f66936e140be0618a60ebf4453289) 👷 Changed step name
- [`a859df7`](https://github.com/maicol07/spraypaintkt/commit/a859df76a29a0364f187059a2642f40a7107f313) Added correct version name when generating docs
- [`1849754`](https://github.com/maicol07/spraypaintkt/commit/1849754949f06755efc0bcea89f9be60cb8e9e5e) Generate docs in case of release

### Other changes

- [`7ba9b57`](https://github.com/maicol07/spraypaintkt/commit/7ba9b57cba9de78946d5d89d4b6eaa6a0099f122) **deps:** ⬆️ Upgraded Gradle to 8.8 and Logback Classic to 1.5.6
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/738a7ff674edd15042d9552e9577bf201e366f69"><code>738a7ff</code></a> 🚚 Moved tests to a separate module</summary>

  > Moved tests from the core module to a separate test module.
  > Added new test cases for error handling
  >  and relationship deserialization.

  </details>
- [`c99d0e1`](https://github.com/maicol07/spraypaintkt/commit/c99d0e1c9dba4fdc631c0f705f60383f1573df59) **deps:** ⬆️ Upgraded KotlinPoet KSP to 1.18.0
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/f76cdf64cc5ef98e61d2d61207d19d77833c4254"><code>f76cdf6</code></a> Add dokka to annotation, ktor-integration and processor module</summary>

  > This commit adds the dokka plugin to all modules and creates a dokkaJar task to generate
  >  javadoc jars from the dokkaHtml output.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9223335d007fd97622af1e72fdf880ce019e6bdb"><code>9223335</code></a> 🔧 Improve Dokka generation</summary>

  > This commit improves the Dokka generation process by:
  >
  > - Consolidating Dokka configuration to the root project'
  > s `build.gradle.kts` file.
  > - Adding source links to the generated documentation, allowing users to easily navigate to the source code on GitHub.
  > - Generating a single "MultiModule" Dokka output, providing a unified view of the project's documentation.
  > - Removing redundant Dokka tasks from
  >  subprojects.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/957b123915cea25b4d425135a41fc5cb0b37d68c"><code>957b123</code></a> Publish snapshots of missing packages to GitHub Packages</summary>

  > This commit adds configuration to publish the artifacts to GitHub Packages. It sets up the repository URL and credentials for authentication.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/cf7278b65f2fdd474d451ab97222c7fd1a6abf84"><code>cf7278b</code></a> Set project version from environment variable</summary>

  > This commit sets the project version from the environment variable `LIB_VERSION`. If the environment variable is not set
  > , the version defaults to "0.1.0".

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/b20d58b864e4f1ab2ad7d2b2bd75b4a6a77029ef"><code>b20d58b</code></a> ⬆️ Update dependencies</summary>

  > This commit updates various dependencies to their latest versions:
  >
  > - Updated AGP to 8.5.2
  > - Updated Kotlin to 2.0.21
  > - Updated KotlinPoet KSP to 1.18.1
  > - Updated Kotlinx Coroutines Test to 1.9.0
  > - Updated Kotlinx Serialization JSON to 1.7.3
  > - Updated Ktor to 3.0.1
  > - Updated Logback Classic to 1.5.12
  > - Updated KSP to 2.0.21-1.0.25
  > - Updated Java compatibility to 21

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/5db53669fbebc6885a08415a31ceb795614cd6af"><code>5db5366</code></a> ⬆️ Update JDK to 21</summary>

  > This commit updates the JDK used in GitHub Actions workflows to version 21.
  >
  > The following workflows are affected:
  >
  > - Test
  > - Release
  > - Snapshot
  > - Docs

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/4ae71532c9b5db1b34d8a21937238459bbd6af82"><code>4ae7153</code></a> <b>deps:</b> ⬆️ Bump dependencies</summary>

  > This commit updates the following dependencies:
  >
  > - Android Gradle Plugin (AGP) to 8.6.1
  > - Kotlin to 2.1.0
  > - KotlinPoet KSP to 2.0.0
  > - Kotlin Symbol Processing (KSP) to 2.1.0-1.0.29

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/0afd2d8ec70ec84ecde16d87036f79103bb6ab2f"><code>0afd2d8</code></a> 🔧 Update settings.gradle.kts</summary>

  > This commit updates the `settings.gradle.kts` file to:
  >
  > - Configure Google repository to include specific group IDs using regex.
  > - Configure dependency resolution management for Google repository to include specific group IDs using regex.
  > - Update the includes statement to include new modules: `:core`, `:ktor-integration`.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/b7457d658d6e2c7716ffb6fc553892bd9598856a"><code>b7457d6</code></a> ✨ Improve Gradle build performance</summary>

  > This commit introduces several changes to improve Gradle build performance:
  >
  > - Increased JVM heap size to 4GB.
  > - Enabled the Gradle configuration cache.
  > - Enabled the Gradle daemon.
  > - Enabled parallel execution of tasks.
  > - Increased the Kotlin daemon heap size to 4GB.

  </details>
- [`de96d7f`](https://github.com/maicol07/spraypaintkt/commit/de96d7fc2e5f4a51c6cac96480e9c278441c07a0) ✨ Added JS yarn.lock
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/e2a27f771011596e7c8fa43f29f01bafcc5080c4"><code>e2a27f7</code></a> 🔧 Disable configuration cache</summary>

  > This commit disables the Gradle configuration cache by setting `org.gradle.configuration-cache` to `false` in the `gradle.properties` file.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/65bc38a6702ee32f1f9dacb1a2b63dd472074064"><code>65bc38a</code></a> 🔧 Trigger workflows on all branches</summary>

  > This commit updates the `test` and `snapshot` workflows to trigger on all branches instead of just the `main` branch.
  >
  > This change allows for more flexible testing and snapshot generation across different branches of the repository.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/6dc86405919aa7fb6235402956bc3b5666f4ada2"><code>6dc8640</code></a> <b>deps:</b> ⬇️ Downgrade Kotlin to 2.0.21</summary>

  > This commit downgrades the Kotlin version to 2.0.21 and updates the KSP version accordingly.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/6d1956f3a0780b63c5f382531f66cf0ca10fd24e"><code>6d1956f</code></a> <b>deps:</b> ⬆️ Update Gradle to 8.10.2 and AGP to 8.7.3</summary>

  > This commit updates the following dependencies:
  >
  > - Gradle Wrapper to 8.10.2
  > - Android Gradle Plugin (AGP) to 8.7.3

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/ef4ca8ea7da9793d0a955ef432780f21a1877d1b"><code>ef4ca8e</code></a> <b>deps:</b> ⬆️ Update dependencies</summary>

  > Update the following dependencies to their latest versions:
  >
  > - Compose to 1.7.3
  > - androidx-activityCompose to 1.9.3
  > - androidx-uiTest to 1.7.6
  > - ktor to 3.0.3
  > - logback-classic to 1.5.15

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/9aa61a09602320a3ee9684504a2a957c2403619e"><code>9aa61a0</code></a> <b>tests:</b> ⏳ Set timeout for tests</summary>

  > This commit sets a timeout for all tests to prevent them from running indefinitely.
  >
  > Specific changes include:
  >
  > - Added a timeout of 100 seconds to all tests in QueryTest, ErrorsTest, WritingTests, and SerializationTest.
  > - Reduced the request timeout in BaseTest to 60 seconds.

  </details>

### ⏪ Reverts

- [`b29e739`](https://github.com/maicol07/spraypaintkt/commit/b29e7397557748ae6b626e3ef76616cc5c63df37) Revert "chore(deps): ⬇️ Downgrade Kotlin to 2.0.21"

<a name="1.0.0"></a>
## [1.0.0](https://github.com/maicol07/spraypaintkt/compare/0.2.0...1.0.0)

> Released on June 26, 2024

### ✨ Features

- [`e27a05a`](https://github.com/maicol07/spraypaintkt/commit/e27a05a1c91ef5f9ce9081b0869ee1d855d71abb) ✨ Added links and meta to resources
- [`67ce951`](https://github.com/maicol07/spraypaintkt/commit/67ce95104b224aefb698e23e99510665d60f84fa) ✨ Added saving and deletion
- [`4fea7e8`](https://github.com/maicol07/spraypaintkt/commit/4fea7e84d6db7632bbcf17134ce82f39edead727) ✨ Added custom JSONAPI Exceptions
- [`7ed88ed`](https://github.com/maicol07/spraypaintkt/commit/7ed88edf46191eca38348152d760dbd0dc3a9554) ✨ Update resource after successful create request
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/4de7ad8ce28517d49bd6ce7d4b468a30a4f6ee3f"><code>4de7ad8</code></a> ✨ Added Dirty attributes and relationships</summary>

  > This helps in some cases where some fields are protected from writing

  </details>

### 🐛 Bug Fixes

- [`0542880`](https://github.com/maicol07/spraypaintkt/commit/0542880b2fcb0e1217213da6e6186cb95f2ded26) Changed `attribute` delegate to allow to set values
- [`a15461a`](https://github.com/maicol07/spraypaintkt/commit/a15461a344c7d5f1a2f907b7235b4c17354c6b04) Ignore unknown keys in JsonApiException decoding
- [`ff00e5b`](https://github.com/maicol07/spraypaintkt/commit/ff00e5b41a677b9780398da2049bf8e8e8dc30a7) Convert incoming attributes to mutable
- [`e68c0d2`](https://github.com/maicol07/spraypaintkt/commit/e68c0d22c96d0d8924ebe13d88a5da6f5c2ae17b) Fix about property required in error
- [`128020b`](https://github.com/maicol07/spraypaintkt/commit/128020bdf2bbf5971efc8f453a41d8f6b1388935) Fix error meta deserialization
- [`54ee133`](https://github.com/maicol07/spraypaintkt/commit/54ee1334da3a83914bf102dcb55d9dfe26b770ba) Send `Content-Type` and `Accept` headers
- [`f5f3268`](https://github.com/maicol07/spraypaintkt/commit/f5f3268a0106b26e0538331abd94b83086583c62) Catch serialization exception in case the body isn't JSON
- [`63c3222`](https://github.com/maicol07/spraypaintkt/commit/63c3222d43da36d9e8d575e43d6911e87b2520c7) Data casting when empty
- [`3ee571b`](https://github.com/maicol07/spraypaintkt/commit/3ee571b233c78e9be63146cffd81359e74bdecdb) To-One Relationship not detected as single when empty
- [`7419460`](https://github.com/maicol07/spraypaintkt/commit/7419460741d121d224f00998908cca32fc8dd38c) To-One Relationship not deserialized as null when empty
- [`4cf4552`](https://github.com/maicol07/spraypaintkt/commit/4cf4552234ff580c732bd22044fe5e81de809f2a) Include 201 created and 202 accepted in save response status code check
- [`884bdd4`](https://github.com/maicol07/spraypaintkt/commit/884bdd4740affe71d6932356ebd9ef86caf2aeb6) Unknown resource object
- [`4b7f2c2`](https://github.com/maicol07/spraypaintkt/commit/4b7f2c222a886f2c543c002e1ce2394c77bdbadd) FIx infinite recursion when related model is the same as the root resource
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/0a7bf314472ee4ea519309c5e98b8fdbf40811be"><code>0a7bf31</code></a> Don&#x27;t use the same Deserializer on different requests</summary>

  > This can lead to use cached resources before they are updated

  </details>
- [`864ddfd`](https://github.com/maicol07/spraypaintkt/commit/864ddfd842ec506108ef6019687c2fc7398f98ce) Track nested changes for dirty saving
- [`dc97ce3`](https://github.com/maicol07/spraypaintkt/commit/dc97ce33e4ea378a8be0baa87dd1a4f234a4312d) Fix some removal cases in DirtyMap and DirtyList

### ⚡ Performance Improvements

- [`46b32f8`](https://github.com/maicol07/spraypaintkt/commit/46b32f8375c92e59cd22d4ab42f098d78d0ba547) ⚡ Optimized properties
- [`b6ad232`](https://github.com/maicol07/spraypaintkt/commit/b6ad2327de5b7ce2bee707530bdb12e3333df315) ⚡ Use assignment

### ♻️ Code Refactoring

- [`19208cf`](https://github.com/maicol07/spraypaintkt/commit/19208cfbffcb27c6f93b4f6c8bc1039b2e854c6f) HTTPClient improvements

### 📝 Docs changes

- [`3797853`](https://github.com/maicol07/spraypaintkt/commit/3797853cef584867d470755f1920213433adf9e8) 📝 Added relationship delegates
- [`3d3302e`](https://github.com/maicol07/spraypaintkt/commit/3d3302e835737847964b312b0f6063cad45e737e) 📝 Documented code
- [`c62d127`](https://github.com/maicol07/spraypaintkt/commit/c62d127cc922c16147ebf64d17114bd810f95099) Added dokka
- [`e9573b8`](https://github.com/maicol07/spraypaintkt/commit/e9573b898a833b8ede79b8a90301d6fcef70fc3c) 📝 Added KDocs

### ✅ Tests

- [`a093611`](https://github.com/maicol07/spraypaintkt/commit/a09361165ba811458b8fc7c36db8dc93a900d541) Disabled filter test due to server issue
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/eb48963a8d5cd45926f77285deaab6730b40572a"><code>eb48963</code></a> Updated tests with example API</summary>

  > Tests now use @thomaxxl example API server: https://thomaxxl.pythonanywhere.com/api
  > This way we can test also save and delete!

  </details>
- [`333a551`](https://github.com/maicol07/spraypaintkt/commit/333a551d8f25d85dae596050bf03f10a8861d33d) Fixed tests ID changing every day
- [`0555ab5`](https://github.com/maicol07/spraypaintkt/commit/0555ab5a63b12e9071a02d86b5b8b270cae4ea90) Added Ktor deep logging to tests

### 👷 CI changes

- [`f7a13f8`](https://github.com/maicol07/spraypaintkt/commit/f7a13f8935e628bc427bc9c65c13c5a8e9b0ec4b) Fix docs action gradlew permission
- [`7d52f14`](https://github.com/maicol07/spraypaintkt/commit/7d52f1463ef9a7cdef6d8381545e8b186955cd57) Upgraded actions

### Other changes

- [`53f7b9a`](https://github.com/maicol07/spraypaintkt/commit/53f7b9a84a114e39986d4839f43d4a9388fc973d) **deps:** ⬆️ Upgraded dependencies
- [`8e5f1f3`](https://github.com/maicol07/spraypaintkt/commit/8e5f1f376d30155181e7f803f667396ce0b08ec0) **deps:** Update AGP
- [`80ebe03`](https://github.com/maicol07/spraypaintkt/commit/80ebe03f0ad7ecff09c5b222e158852058a230f6) **deps:** ⬆️ Upgraded AGP
- [`6888792`](https://github.com/maicol07/spraypaintkt/commit/68887922aec71b6693cadc0d4466bf267aeaf8ab) **deps:** Updated dependencies
- [`f052997`](https://github.com/maicol07/spraypaintkt/commit/f052997672858190bff58803286a1e2612630f01) **deps:** ⬆️ Upgraded AGP and dependencies
- [`f0f3fce`](https://github.com/maicol07/spraypaintkt/commit/f0f3fce2f1d96855acc0892f4776fb6992112e6a) **deps:** ⬆️ Upgraded dependencies
- [`fe20000`](https://github.com/maicol07/spraypaintkt/commit/fe200008cbbfad43fd156935ba13fedc34837893) **deps:** ⬆️ Upgraded dependencies
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/25b289511b13de734e8f4103ab210e0dbe4ae800"><code>25b2895</code></a> <b>deps:</b> ⬆️ Update dependencies</summary>

  > Updated AGP to 8.5.0, Kotlin to 2.0.0, kotlinx
  > -coroutines-test to 1.9.0-RC, kotlinx-serialization-json to 1.7.1, Ktor to 2.3.12, and maven-publish to 0.29.0.
  > Also updated JVM target to 17.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/bc367bc99961db5e27d287842e985836e3518b32"><code>bc367bc</code></a> 🔧 Update base URL for tests</summary>

  > The base URL for tests has been updated from `https://thomaxxl.pythonanywhere.com/
  > api` to `https://safrs.onrender.com/api`.

  </details>
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/b4a60780c47effefe680be1e23ca90957fca9b6b"><code>b4a6078</code></a> 🤖 Update .gitignore</summary>

  > Updated the .gitignore file to include a more comprehensive list of files and directories to ignore, generated by Top
  > tal's gitignore.io service. This helps to keep the repository clean and prevent unnecessary files from being tracked.

  </details>

<a name="0.2.0"></a>
## [0.2.0](https://github.com/maicol07/spraypaintkt/compare/0.1.0...0.2.0)

> Released on March 12, 2024

### ✨ Features

- [`f3ff982`](https://github.com/maicol07/spraypaintkt/commit/f3ff98212ead977a3461aa64a3e052d0b2df5fe5) ✨ Add new delegate to use hasManyRelationships

### 🐛 Bug Fixes

- [`50d5b0d`](https://github.com/maicol07/spraypaintkt/commit/50d5b0def6fd205fc651b863bf7a19ef54fd283f) HasMany relation deserialized as hasOne
- [`3c3b80b`](https://github.com/maicol07/spraypaintkt/commit/3c3b80b678bf74b3d85d2d7b0a1fd0e8254e87ca) Fix URL path generation
- [`e2fa768`](https://github.com/maicol07/spraypaintkt/commit/e2fa768b379d2ba1aeddebb48ccdb7e968fbb04a) Fix pluralize not working correctly in most cases

### ♻️ Code Refactoring

- [`c455e45`](https://github.com/maicol07/spraypaintkt/commit/c455e4564986c251bc0e9b85059c12bffb677621) Change package path

### 📝 Docs changes

- [`fd3de14`](https://github.com/maicol07/spraypaintkt/commit/fd3de146cd2abd81a4fdb9b71f2a5d88010c6a63) 📝 Add snapshot section
- [`c0cf0c6`](https://github.com/maicol07/spraypaintkt/commit/c0cf0c6e93d0ff44302a6b9a7e6358422538ad85) 📝 Fix missing anchor links

### ✅ Tests

- [`d0b055b`](https://github.com/maicol07/spraypaintkt/commit/d0b055b681b11ecb9161afb4813e2ad17f190ba5) Removed core sample tests
- [`2242dba`](https://github.com/maicol07/spraypaintkt/commit/2242dba5406a8caff72ec14ac9ee8b276d70c1de) ✅ Added tests
- [`c33acbf`](https://github.com/maicol07/spraypaintkt/commit/c33acbf6bde6cb38e7a97ef3f9cc934df87bc636) Removed specific resources types

### 👷 CI changes

- [`a006702`](https://github.com/maicol07/spraypaintkt/commit/a006702850aa987282c4092fa9629c251deb1a29) Implemented CI snapshot builds and publishing
- [`89dbbb4`](https://github.com/maicol07/spraypaintkt/commit/89dbbb4d79456c66bbf5d72d0c88e0552fd06b11) Fix branch name
- [`f3c484e`](https://github.com/maicol07/spraypaintkt/commit/f3c484e22dbfd6bb0050d22ae41ccb4dfec629d7) Fix gradle setup (permission denied)
- [`02473c1`](https://github.com/maicol07/spraypaintkt/commit/02473c1dc8eaae28cf530480124be177845fd823) Fix permission denied on gradlew
- [`51c3b38`](https://github.com/maicol07/spraypaintkt/commit/51c3b388a7757c0b7ab6a2e78ab853917931ec6c) Fix echos not appending in multiple lines
- [`d9459f4`](https://github.com/maicol07/spraypaintkt/commit/d9459f400c7e0facbd5327eb8c78e90dca706f20) Fix multiple commands syntax
- [`0508fc5`](https://github.com/maicol07/spraypaintkt/commit/0508fc5fe542079a48b37ec80e4c53c208e34eb8) Add gradle signing properties to user home
- [`b53f7fa`](https://github.com/maicol07/spraypaintkt/commit/b53f7fac32971f4d412ba57d7a1a49a5b36d35c8) Fix sync command
- [`7c4a649`](https://github.com/maicol07/spraypaintkt/commit/7c4a64917b5a694cf3a977a23029f6d5aaf93def) Disabled sync
- [`ddea898`](https://github.com/maicol07/spraypaintkt/commit/ddea8985c02c40827f87900386965ab7cb0cdb3b) Use env variables instead of gradle properties file
- [`8396baa`](https://github.com/maicol07/spraypaintkt/commit/8396baa6f86d249c088499877ab41f6c78966fd9) Use new secret for armored key
- [`36bdf89`](https://github.com/maicol07/spraypaintkt/commit/36bdf8997dfa66b14d343093a013a939f1dc5536) Restore gradle properties signing config
- [`1c69bb2`](https://github.com/maicol07/spraypaintkt/commit/1c69bb21f88f14ab3dca83026bc0ac47ed03bec6) GPG testing
- [`287d996`](https://github.com/maicol07/spraypaintkt/commit/287d9962ac211647679236115a5e4a71ead4c399) Fix env name
- [`73b73d1`](https://github.com/maicol07/spraypaintkt/commit/73b73d1e0534f5b50ccbc72fb89f15190b5a59bb) Send snapshots to GH packages
- [`b5c6a78`](https://github.com/maicol07/spraypaintkt/commit/b5c6a785d24a8884cc8a0f276560d7df43e920c5) Fix GH packages auth
- <details><summary><a href="https://github.com/maicol07/spraypaintkt/commit/94d3fcb4a89a8ba2a6edb209dcf0cea4324c5783"><code>94d3fcb</code></a> Update actions version</summary>

  > Fix Node.JS deprecated versions

  </details>
- [`4d2773f`](https://github.com/maicol07/spraypaintkt/commit/4d2773f64b14b6988e7e12063dea7f4f215bc2e1) Move env to related steps
- [`5d2d253`](https://github.com/maicol07/spraypaintkt/commit/5d2d25363ea18e17921e947017ecf6d0b8bd85fe) Fix missing Java distribution
- [`7191091`](https://github.com/maicol07/spraypaintkt/commit/7191091b7199ae6e34e69c8f9c37070cf5c6c2fe) Fix short SHA extraction
- [`5707f9d`](https://github.com/maicol07/spraypaintkt/commit/5707f9dc4e969a44190efd3fae194a1ef077932f) Merge build and publish
- [`ddf7b89`](https://github.com/maicol07/spraypaintkt/commit/ddf7b899862016163f087b50043440bf3241de02) Fix variable for actor
- [`6b848c5`](https://github.com/maicol07/spraypaintkt/commit/6b848c5fea28d5a7dfed1115e58d09877e5a797a) Fix gh packages username
- [`3e481f3`](https://github.com/maicol07/spraypaintkt/commit/3e481f3dac649ae96f9e71d573cf6b7308471a18) Move upload artifact after publish
- [`0dc68b3`](https://github.com/maicol07/spraypaintkt/commit/0dc68b379c04b315d8c1fee0d0e80788e3ba6f21) Add permissions to GITHUB_TOKEN
- [`d30b2a1`](https://github.com/maicol07/spraypaintkt/commit/d30b2a1d94f071e84d228199a294ba554d1655a5) 👷 Added Maven Central release workflow
- [`14cf964`](https://github.com/maicol07/spraypaintkt/commit/14cf964f409b176f70d9fb4ca0aa38f4ec23c9d0) 💚 Moved GH action token permissions up

### Other changes

- [`495ccdf`](https://github.com/maicol07/spraypaintkt/commit/495ccdf962d0990f4cc313adadbe300598a42c89) Suppress targets and AGP warnings
- [`585f7e2`](https://github.com/maicol07/spraypaintkt/commit/585f7e2b038b8530f296c9463ec2267f2b7ea1db) Remove multiple library variants
- [`a436d5b`](https://github.com/maicol07/spraypaintkt/commit/a436d5be3cdbd88cf262ccafa961844e2e7fdf40) **deps:** ✨ Removed CIO dependency
- [`807fa10`](https://github.com/maicol07/spraypaintkt/commit/807fa1075d079f2012d2eeddd15f6896031d5ace) **deps:** Updated AGP
- [`6b97411`](https://github.com/maicol07/spraypaintkt/commit/6b974113683d51743390ede491dfe1c8609555c3) **deps:** Updated dependencies
- [`cd24f94`](https://github.com/maicol07/spraypaintkt/commit/cd24f94b106354e69734d280e6d0e473e9a8bc70) **deps:** Revisioned dependencies

### ⏪ Reverts

- [`43af6c3`](https://github.com/maicol07/spraypaintkt/commit/43af6c3a7020a3aa61b41a14f708134e8266222a) Revert "chore: Remove multiple library variants"

<a name="0.1.0"></a>
## 0.1.0

> Released on March 03, 2024

### ✨ Features

- [`41c32ec`](https://github.com/maicol07/spraypaintkt/commit/41c32ecdc5f8fad87cce60b310bbc6ad2c769581) Initial commit

### ♻️ Code Refactoring

- [`dbc4129`](https://github.com/maicol07/spraypaintkt/commit/dbc4129db3fdf5f5b0aea09b95d8e2916179a85e) ♻️ Removed baseUrl default

### 📝 Docs changes

- [`8748f78`](https://github.com/maicol07/spraypaintkt/commit/8748f78ee2cd439b0a055ea6956656ef532dd59d) 📝 Added README

### 👷 Building scripts changes

- [`775b6ff`](https://github.com/maicol07/spraypaintkt/commit/775b6ff7edaca4856bfa4436ce576d4529949cc4) Fix build not using group
- [`c5f50cf`](https://github.com/maicol07/spraypaintkt/commit/c5f50cff7342eb2a24368967314d3e6a0a4e8226) Added publishing config
- [`1f3b473`](https://github.com/maicol07/spraypaintkt/commit/1f3b473b120be8d5bb63de99375679d1e0f45584) Fix Gradle sync/build issue

### Other changes

- [`c1558bb`](https://github.com/maicol07/spraypaintkt/commit/c1558bb502d248278b1c1d61c0567adc0020f4e4) Updated namespace
- [`0f2709d`](https://github.com/maicol07/spraypaintkt/commit/0f2709d0762fe520344abe8bcb7209e5c11cc17c) Removed unused test file
- [`1930ad1`](https://github.com/maicol07/spraypaintkt/commit/1930ad18ad100d5605a71375d804bf33305a18ba) Added LICENSE
- [`5ecd4a4`](https://github.com/maicol07/spraypaintkt/commit/5ecd4a4748f29597480ebea7435a5d5a524483dd) Upgrade AGP

