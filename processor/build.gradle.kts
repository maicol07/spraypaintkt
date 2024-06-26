import com.vanniktech.maven.publish.SonatypeHost

group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!

plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.mavenPublish)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // KSP
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.22")
    // KotlinPoet
    implementation("com.squareup:kotlinpoet:1.17.0")
    implementation("com.squareup:kotlinpoet-ksp:1.17.0")

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("dev.zacsweers.autoservice:auto-service-ksp:1.10")
    implementation(projects.annotation)

    /* TEST  */
//    testImplementation(libs.junit)
//    testImplementation(libs.kctfork.core)
//    testImplementation(libs.kctfork.ksp)
//    testImplementation(libs.mockito.kotlin)
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