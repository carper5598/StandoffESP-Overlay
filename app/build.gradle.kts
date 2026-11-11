plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.esp.overlay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.esp.overlay"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
