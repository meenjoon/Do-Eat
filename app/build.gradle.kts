import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())
val keystoreProperties = Properties()
keystoreProperties.load(rootProject.file("keystore.properties").inputStream())

val KAKAO_SIGNIN_NATIVE_KEY = properties.getProperty("kakao_signin_native_key")
val NAVER_CLIENT_ID = properties.getProperty("naver_client_id")

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }
    namespace = "com.mbj.doeat"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.mbj.doeat"
        minSdk = 24
        targetSdk = 33
        versionCode = 7
        versionName = "1.2"
        setProperty("archivesBaseName", "${applicationId}-v${versionName}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "KAKAO_NATIVE_KEY", properties.getProperty("kakao_native_key"))
        buildConfigField(
            "String",
            "NAVER_OEPNAPI_BASE_URL",
            properties.getProperty("naver_openapi_base_url")
        )
        buildConfigField(
            "String",
            "NAVER_OEPNAPI_CLIENT_ID",
            properties.getProperty("naver_openapi_client_id")
        )
        buildConfigField(
            "String",
            "NAVER_OEPNAPI_CLIENT_SECRET",
            properties.getProperty("naver_openapi_client_secret")
        )
        buildConfigField(
            "String",
            "NAVER_SEARCH_BASE_URL",
            properties.getProperty("naver_search_base_url")
        )
        buildConfigField("String", "DOEAT_BASE_URL", properties.getProperty("doeat_base_url"))

        manifestPlaceholders["KAKAO_SIGNIN_NATIVE_KEY"] = KAKAO_SIGNIN_NATIVE_KEY
        manifestPlaceholders["NAVER_CLIENT_ID"] = NAVER_CLIENT_ID
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            versionNameSuffix = "-release"
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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.2")

    // Compose
    implementation("androidx.compose.ui:ui:1.3.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.2")
    implementation("androidx.compose.material:material:1.0.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.5.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.45")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.45")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")

    // KaKao Login
    implementation("com.kakao.sdk:v2-user:2.13.0")

    // Naver Map
    implementation("com.naver.maps:map-sdk:3.17.0")
    implementation("io.github.fornewid:naver-map-compose:1.3.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // WebView
    implementation("com.google.accompanist:accompanist-webview:0.24.13-rc")

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
}
