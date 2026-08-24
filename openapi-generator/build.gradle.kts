group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!

plugins {
    kotlin("jvm")
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.dokkatoo.html)
    alias(libs.plugins.kotest)
}

dependencies {
    implementation(libs.swagger.parser)
    implementation(libs.kotlinpoet)
    implementation(libs.kasechange)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.engine)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

/**
 * Writes the schemas described by an OpenAPI document into a source directory:
 *
 * ```shell
 * ./gradlew :openapi-generator:generateSchemas \
 *   -Pinput=https://api.example.com/openapi.yaml \
 *   -Ppackage=com.example.models \
 *   -Poutput=app/src/commonMain/kotlin
 * ```
 */
tasks.register<JavaExec>("generateSchemas") {
    group = "code generation"
    description = "Generates Spraypaint.Kt resource schemas from a JSON:API OpenAPI document"
    mainClass = "it.maicol07.spraypaintkt_openapi.SpraypaintSchemaGeneratorKt"
    classpath = sourceSets.main.get().runtimeClasspath
    // Relative input/output paths are resolved against the repository root, not this module.
    workingDir = rootDir
    argumentProviders.add(
        CommandLineArgumentProvider {
            fun property(name: String) = providers.gradleProperty(name).orNull
                ?: error("Missing required Gradle property -P$name.")
            listOf(
                "--input", property("input"),
                "--package", property("package"),
                "--output", property("output"),
            )
        }
    )
}

mavenPublishing {
    publishToMavenCentral(validateDeployment = com.vanniktech.maven.publish.DeploymentValidation.NONE)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name = "Spraypaint.Kt - OpenAPI schema generator"
        description = "Generates Spraypaint.Kt resource schemas from a JSON:API OpenAPI document"
        inceptionYear = "2026"
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
            credentials(PasswordCredentials::class)
        }
    }
}

kotest {
    customGradleTask = true
}
