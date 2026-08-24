plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

android {
    namespace = "it.maicol07.spraypaintkt.sample.androidApp"
    compileSdk = 37

    defaultConfig {
        applicationId = "it.maicol07.spraypaintkt.sample.androidApp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(projects.sample.composeApp)
    implementation(libs.androidx.activityCompose)
    implementation(libs.ktor.client.cio)
    implementation(compose.uiTooling)

    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
}
