import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URI

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary) apply false
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp) apply false
}

rootProject.extra.set("libVersion", System.getenv("LIB_VERSION") ?: "0.1.0")

version = rootProject.extra.get("libVersion")!!

subprojects {
    apply(plugin = "org.jetbrains.dokka")

    tasks {
        register<Jar>("dokkaJar") {
            from(dokkaHtml)
            dependsOn(dokkaHtml)
            archiveClassifier.set("javadoc")
        }
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            // Read docs for more details: https://kotlinlang.org/docs/dokka-gradle.html#source-link-configuration
            sourceLink {
                localDirectory.set(rootProject.projectDir)
                remoteUrl.set(URI.create("https://github.com/maicol07/spraypaintkt").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}

// Configures only the parent MultiModule task,
// this will not affect subprojects
tasks.dokkaHtmlMultiModule {
    moduleName.set("Spraypaint.Kt")
}