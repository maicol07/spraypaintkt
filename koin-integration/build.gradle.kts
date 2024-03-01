import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("com.vanniktech.maven.publish") version "0.27.0"
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


mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name = "Spraypaint.Kt - Koin Integration"
        description = "Integration with Koin for Spraypaint.Kt"
        inceptionYear = "2024"
        url = "https://github.com/maicol07/spraypaintkt"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "maicol07"
                name = "Maicol Battistini"
                url = "https://maicol07.it"
            }
        }
        scm {
            url = "https://github.com/maicol07/spraypaintkt"
            connection = "scm:git:git://github.com/maicol07/spraypaintkt.git"
            developerConnection = "scm:git:ssh://git@github.com/maicol07/spraypaintkt.git"
        }
    }
}