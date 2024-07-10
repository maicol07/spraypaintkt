import com.vanniktech.maven.publish.SonatypeHost

group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!

plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    alias(libs.plugins.mavenPublish)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(projects.annotation)
    api(projects.core)
    // KSP
    implementation(libs.symbol.processing.api)
    implementation(libs.kotlinpoet.ksp)

    ksp(libs.auto.service.ksp)
    // NOTE: It's important that you _don't_ use compileOnly here, as it will fail to resolve at compile-time otherwise
    implementation(libs.auto.service.annotations)
    implementation(kotlin("reflect"))
    implementation(libs.kasechange)
    implementation(libs.kotlinx.serialization.json)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name = "Spraypaint.Kt - Ktor Integration"
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