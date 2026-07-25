plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.echosmart.flashlabs"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.echosmart.flashlabs"
        minSdk = 26
        targetSdk = 36
        versionCode = 100
        versionName = "1.0.0-pro"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("releaseConfig") {
            storeFile = file("../.github/keystore/flashlabs.jks")
            storePassword = "flashlabspass"
            keyAlias = "flashlabs_key"
            keyPassword = "flashlabspass"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("releaseConfig")
            buildConfigField("Boolean", "ENABLE_DEBUG_LOGS", "true")
            buildConfigField("String", "BUILD_MODE", "\"DEBUG_VERBOSE\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("releaseConfig")
            buildConfigField("Boolean", "ENABLE_DEBUG_LOGS", "false")
            buildConfigField("String", "BUILD_MODE", "\"RELEASE_PRODUCTION\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    // Core AndroidX & Lifecycle
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Jetpack Compose & Material 3
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Preferences & Async
    implementation("androidx.datastore:datastore-preferences:1.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
}
