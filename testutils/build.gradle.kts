plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.klynaf.testutils"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":core"))

    api(libs.junit.jupiter.api)
    api(libs.mockk)
    api(libs.coroutines.test)
    api(libs.turbine)

    runtimeOnly(libs.junit.jupiter.engine)
    runtimeOnly(libs.junit.platform.launcher)
}
