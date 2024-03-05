enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "spraypaintkt"

include(":core")
include(":ktor-integration")
include(":koin-integration")
include(":annotation")
include(":processor")
