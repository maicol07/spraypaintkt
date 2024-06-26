import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    jvmToolchain(17)
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release", "debug")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "annotation"
        }
    }
}

group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!

android {
    namespace = "it.maicol07.spraypaintkt_annotation"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name = "Spraypaint.Kt - Annotation"
        description = "Integration with Ktor for Spraypaint.Kt"
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