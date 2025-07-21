plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.cryptofrontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cryptofrontend"
        minSdk = 28
        targetSdk = 34
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    // ✅ Firebase Messaging for push notifications
    implementation("com.google.firebase:firebase-messaging:23.4.1")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ✅ Compose BOM & UI
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ✅ Android essentials
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    // ✅ Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // ✅ JSON parsing
    implementation("org.json:json:20230618")

    // ✅ Glide image loading
    implementation(libs.glide)
    implementation(libs.firebase.inappmessaging.display)
    kapt(libs.glide.compiler)

    // ✅ Web3j for Ethereum & smart contracts
    implementation("org.web3j:core:4.9.8")
    implementation("org.web3j:crypto:4.9.8")

    // ✅ WebView support (MetaMask integration)
    implementation("androidx.webkit:webkit:1.8.0")

    // ✅ Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // ✅ Bouncy Castle cryptography
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    // ✅ Browser for OAuth or MetaMask linking
    implementation("androidx.browser:browser:1.7.0")

    // ✅ Unit Test
    testImplementation("junit:junit:4.13.2")

    // ✅ Android Instrumentation Test
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ✅ Compose UI Testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
