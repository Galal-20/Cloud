import org.jetbrains.kotlin.gradle.model.Kapt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt") // Correct way to apply the kapt plugin


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
        /*buildConfigField("String", "API_KEY", "6a482dc37ff81d4d3deec39521543316")
        buildConfigField("String", "UNITS", "metric")*/


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
        buildConfig = true


    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Use kapt for Room

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
    annotationProcessor(libs.compiler)


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

    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation(libs.taptargetview)

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.6.1")
    testImplementation("org.mockito:mockito-inline:4.6.1")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("org.robolectric:robolectric:4.10")






}