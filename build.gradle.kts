import java.util.Properties

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    id("com.gradleup.nmcp").version("0.0.4")
    id("com.vanniktech.maven.publish") version "0.27.0" apply false
}

nmcp {
    publishAllProjectsProbablyBreakingProjectIsolation {
        val localSecrets = project.file("local.properties")
        if (localSecrets.exists()) {
            val localProperties = Properties()
            localProperties.load(localSecrets.inputStream())
            username = localProperties.getProperty("mavenCentralUsername")
            password = localProperties.getProperty("mavenCentralPassword")
        } else {
            username = ""
            password = ""
        }
        // publish manually from the portal
        publicationType = "USER_MANAGED"
        // or if you want to publish automatically
        publicationType = "AUTOMATIC"
    }
}