plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin.compose)
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
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    // ✅ HTTP client for API calls
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // ✅ JSON parsing - use the correct dependency
    implementation("org.json:json:20230618")

    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // ✅ Web3j for Ethereum blockchain interaction
    implementation("org.web3j:core:4.9.8")
    implementation("org.web3j:crypto:4.9.8")

    // ✅ WebView for MetaMask integration
    implementation("androidx.webkit:webkit:1.8.0")

    // ✅ Coroutines for async operations
    implementation(libs.kotlinx.coroutines.android)

    // ✅ Additional dependencies for crypto operations
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    // ✅ For better WebView JavaScript handling
    implementation("androidx.browser:browser:1.7.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}