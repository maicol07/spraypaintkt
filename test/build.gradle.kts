plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
    alias(libs.plugins.kotest)
}

dependencies {
    testImplementation(projects.core)
    testImplementation(projects.ktorIntegration)
    testImplementation(projects.annotation)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.cio)
    testImplementation(libs.logback.classic)
    testImplementation(libs.ktor.client.logging)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.engine)
    ksp(projects.processor)
    testImplementation(libs.kotlinx.serialization.json)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
