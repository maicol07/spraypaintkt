plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  id("maven-publish")
}

kotlin {
  androidTarget {
    compilations.all {
      kotlinOptions {
        jvmTarget = "1.8"
      }
      publishLibraryVariants("release", "debug")
    }
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "ktor-integration"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      api(projects.core)
      implementation(libs.ktor.client.core)
      implementation(libs.ktor.client.cio)
    }
  }
}

android {
  namespace = "it.maicol07.spraypaintkt_ktor_integration"
  compileSdk = 34
}
