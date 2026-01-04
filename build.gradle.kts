import java.net.URI

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary) apply false
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.dokkatoo.html)
    alias(libs.plugins.dokkatoo.javadoc)
    alias(libs.plugins.ksp) apply false
//    `dokkatoo-convention`

    // Sample
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
}

rootProject.extra.set("libVersion", System.getenv("LIB_VERSION") ?: "0.1.0")

version = rootProject.extra.get("libVersion")!!

dependencies {
    dokkatoo(project(":annotation"))
    dokkatoo(project(":core"))
    dokkatoo(project(":ktor-integration"))
    dokkatoo(project(":processor"))
}

dokkatoo {
    moduleName.set("SpraypaintKT")
    moduleVersion.set(version as String)

    dokkatooSourceSets.configureEach {
        // Read docs for more details: https://kotlinlang.org/docs/dokka-gradle.html#source-link-configuration
        sourceLink {
            localDirectory = rootProject.projectDir
            remoteUrl = URI.create("https://github.com/maicol07/spraypaintkt")
            remoteLineSuffix = "#L"
        }
    }
}
