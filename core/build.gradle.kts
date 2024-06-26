import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.dokka)
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!

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
            baseName = "core"
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/commonMain/kotlin")
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            api(projects.ktorIntegration)
            implementation(projects.annotation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.logback.classic)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", projects.processor)
}

android {
    namespace = "it.maicol07.spraypaintkt"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name = "Spraypaint.Kt"
        description = "A Kotlin Multiplatform library to interact with JSON:API compliant APIs"
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

publishing {
    repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/maicol07/spraypaintkt")
            // username and password (a personal Github access token) should be specified as
            // `githubPackagesUsername` and `githubPackagesPassword` Gradle properties or alternatively
            // as `ORG_GRADLE_PROJECT_githubPackagesUsername` and `ORG_GRADLE_PROJECT_githubPackagesPassword`
            // environment variables
            credentials(PasswordCredentials::class)
        }
    }
}

tasks{
    register<Jar>("dokkaJar") {
        from(dokkaHtml)
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}