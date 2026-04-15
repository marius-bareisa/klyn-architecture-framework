plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.klynaf.core"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(libs.coroutines.core)
    testImplementation(project(":testutils"))
}
