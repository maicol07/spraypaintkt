plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

group = "it.maicol07.spraypaintkt"
version = "0.1.0"