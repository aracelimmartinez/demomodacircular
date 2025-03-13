buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }

    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}

plugins {
    id("com.android.application") version "8.8.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}




