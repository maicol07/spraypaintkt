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
    testImplementation(libs.kotlinx.coroutines.test)
    ksp(projects.processor)
    testImplementation(libs.kotlinx.serialization.json)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
