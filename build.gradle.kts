// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://storage.googleapis.com/r8-releases/raw")
    }

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.45")
        classpath("com.android.tools:r8:8.2.16-dev")
        classpath ("org.jetbrains.kotlin:kotlin-serialization:1.4.0")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

plugins {
    id("com.android.application") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}
