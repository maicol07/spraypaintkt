plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
}

dependencies {
    testImplementation(projects.core)
    testImplementation(projects.ktorIntegration)
    testImplementation(projects.annotation)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.cio)
    testImplementation(libs.logback.classic)
    testImplementation(libs.ktor.client.logging)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    ksp(projects.processor)
    testImplementation(libs.kotlinx.serialization.json)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}