import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.mavenPublish)
}

group = "it.maicol07.spraypaintkt"
version = rootProject.extra.get("libVersion")!!

kotlin {
  jvmToolchain(21)
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
    }
    publishLibraryVariants("release", "debug")
  }
  jvm()
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
      baseName = "ktor-integration"
    }
  }


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

  sourceSets {
    commonMain.dependencies {
      api(projects.core)
      implementation(libs.ktor.client.core)
    }
  }
}

android {
  namespace = "it.maicol07.spraypaintkt_ktor_integration"
  compileSdk = 34
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
