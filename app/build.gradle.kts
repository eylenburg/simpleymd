plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// CI supplies these. A local release build stays unsigned when they are absent.
val releaseKeystoreFile = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
val releaseKeystorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull

android {
    namespace = "com.github.eylenburg.simpleymd"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.github.eylenburg.simpleymd"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        if (!releaseKeystoreFile.isNullOrBlank()) {
            check(!releaseKeystorePassword.isNullOrBlank()) {
                "ANDROID_KEYSTORE_PASSWORD is required when ANDROID_KEYSTORE_FILE is set"
            }
            check(!releaseKeyAlias.isNullOrBlank()) {
                "ANDROID_KEY_ALIAS is required when ANDROID_KEYSTORE_FILE is set"
            }
            check(!releaseKeyPassword.isNullOrBlank()) {
                "ANDROID_KEY_PASSWORD is required when ANDROID_KEYSTORE_FILE is set"
            }
            create("release") {
                storeFile = file(releaseKeystoreFile)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            if (!releaseKeystoreFile.isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
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
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
}
