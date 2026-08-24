import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.dokkatoo.html)
}

kotlin {
    jvmToolchain(21)
    android {
        namespace = "it.maicol07.spraypaintkt_annotation"
        compileSdk = 37
        minSdk = 26
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    jvm()
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }
    linuxX64()
    mingwX64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64()
    ).forEach {
        it.binaries.framework {
            baseName = "annotation"
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core)
        }
    }
}

group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!


mavenPublishing {
    publishToMavenCentral(validateDeployment = com.vanniktech.maven.publish.DeploymentValidation.NONE)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name = "Spraypaint.Kt - Annotation"
        description = "Schema annotations for Spraypaint.Kt"
        inceptionYear = "2024"
        url = "https://github.com/maicol07/spraypaintkt"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
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
