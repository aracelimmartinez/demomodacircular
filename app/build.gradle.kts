plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Users\\arace\\.android\\debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    namespace = "com.example.modacircularra"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.modacircularra"
        minSdk = 29
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.7.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.8.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.android.gms:play-services-cast-framework:21.5.0")
        implementation("com.google.firebase:firebase-storage-ktx:21.0.1")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.android.gms:play-services-auth:21.3.0")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-dynamic-links-ktx")
        implementation("androidx.activity:activity-ktx:1.9.3")
        implementation("com.github.bumptech.glide:glide:4.16.0")
        implementation("androidx.credentials:credentials:1.3.0")
        implementation ("com.facebook.android:facebook-login:18.0.2")

        implementation("io.github.sceneview:arsceneview:0.10.0")
    }
}
