plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
   // id("com.android.application")
}

android {
    namespace = "com.example.e_exam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.e_exam"
        minSdk = 26
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.compose.theme.adapter)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-firestore:24.0.2")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.firebase:firebase-messaging")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.1")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}



