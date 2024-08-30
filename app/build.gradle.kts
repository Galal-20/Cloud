import org.jetbrains.kotlin.gradle.model.Kapt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.cloud"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cloud"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Room
    implementation (libs.androidx.room.ktx)
    implementation (libs.androidx.room.runtime)

    //Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson.v290)

    //GSON
    implementation (libs.gson)
    implementation (libs.retrofit2.converter.gson)

    //Coroutines Dependencies
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    //glide
    implementation (libs.glide)


    implementation (libs.androidx.navigation.fragment)
    implementation (libs.androidx.navigation.ui)

    //openstreetmap
    implementation (libs.osmdroid.android)



    //for Kotlin + workManager
    implementation (libs.androidx.work.runtime.ktx)

    //google_map_service
    implementation (libs.play.services.maps)
    //bottom_sheet
    implementation (libs.material.v120alpha02)



    implementation(libs.play.services.maps.v1810)

    //Timber
    //implementation (libs.timber)

    implementation ("com.airbnb.android:lottie:6.1.0")

    implementation (libs.airlocation)
    implementation("com.daimajia.androidanimations:library:2.4@aar")

    // ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")

    implementation("androidx.activity:activity-ktx:1.7.2") // Use the latest version available





}