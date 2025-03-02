plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id ("org.jetbrains.kotlin.plugin.serialization")
//    id("com.google.protobuf") version "0.9.4"
}

android {
    namespace = "com.data.chatappai"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.data.chatappai"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY")}\"")

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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation (libs.androidx.hilt.navigation.compose)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")



    // Retrofit
    val retrofit_ver = "2.9.0"
    implementation ("com.squareup.retrofit2:retrofit:$retrofit_ver")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit_ver")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")


    val room_ver = "2.6.1"
    kapt ("androidx.room:room-compiler:$room_ver")
    implementation ("androidx.room:room-ktx:$room_ver")

    val coroutines_ver = "1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_ver")
    // Coil
    val coil_ver = "3.0.0"
    implementation("io.coil-kt.coil3:coil-compose:$coil_ver")
    implementation("io.coil-kt.coil3:coil-network-okhttp:$coil_ver")

    implementation ("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation ("com.google.android.gms:play-services-auth:20.3.0")


    implementation ("com.google.accompanist:accompanist-flowlayout:0.31.1-alpha")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

}