plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.klynaf"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.klynaf"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":uicore"))
    implementation(project(":tmdb-impl"))
    implementation(project(":database-impl"))
    implementation(project(":data"))
    implementation(project(":feature-home"))
    implementation(project(":feature-detail"))
    implementation(project(":feature-search"))
    implementation(project(":feature-watchlist"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.compose.material.icons.core)
    implementation(libs.lifecycle.runtime.compose)
    ksp(libs.hilt.compiler)
}
