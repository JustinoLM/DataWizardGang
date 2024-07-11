plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.proyecto.agroinsight"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.proyecto.agroinsight"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        resValue("string", "app_name", "AgroInsight")

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("org.tensorflow:tensorflow-lite:2.15.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("org.apache.commons:commons-lang3:3.12.0")
}
