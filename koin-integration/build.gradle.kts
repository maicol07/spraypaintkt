plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

group = "it.maicol07.spraypaintkt"
version = "0.1.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
            publishLibraryVariants("release", "debug")
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "koin-integration"
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "it.maicol07.spraypaintkt_koin_integration"
    compileSdk = 34
}
