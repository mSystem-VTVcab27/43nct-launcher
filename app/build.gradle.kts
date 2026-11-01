plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.vctv.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vctv.launcher"
        minSdk = 30
        targetSdk = 34
        versionCode = 11
        versionName = "1.1.0-secondary"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.leanback:leanback-tab:1.1.0-beta01")
    implementation("androidx.leanback:leanback-preference:1.1.0-alpha03")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}

