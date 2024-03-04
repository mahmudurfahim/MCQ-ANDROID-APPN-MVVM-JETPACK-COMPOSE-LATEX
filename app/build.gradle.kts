plugins {
    id("com.android.application")
    id ("com.google.gms.google-services")
    id ("androidx.navigation.safeargs")

}

android {
    namespace = "com.example.mcqapp"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.mcqapp"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "34.0.0"
}

dependencies {





    implementation("androidx.lifecycle:lifecycle-livedata-ktx: 2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx: 2.2.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")





    implementation ("com.google.dagger:dagger:2.45")




    implementation("androidx.navigation:navigation-fragment: 2.7.7")
    implementation("androidx.navigation:navigation-ui: 2.7.7")

    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}

