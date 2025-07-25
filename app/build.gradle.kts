plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.bookreaderapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bookreaderapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        viewBinding = true

    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Core Compose Libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.compose.foundation:foundation:1.6.0")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.2")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")

    // Pager
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Google Sign-In and Credential
    implementation("androidx.credentials:credentials:1.2.0-alpha03")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0-alpha03")
    implementation("com.google.android.gms:play-services-auth:20.7.0") // for google sign in

    // PDF Viewing Support
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("io.github.chrisbanes:PhotoView:2.3.0")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
