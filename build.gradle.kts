import java.util.Properties

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary) apply false
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    alias(libs.plugins.mavenPublish) apply false
    id("org.jetbrains.dokka") version "1.9.10" apply false
}

rootProject.extra.set("libVersion", System.getenv("LIB_VERSION") ?: "0.1.0")