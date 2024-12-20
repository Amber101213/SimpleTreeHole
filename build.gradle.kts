plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt") // 确保添加这行
}

android {
    namespace = "com.example.zyl_241213_1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.zyl_241213_1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    }
    kapt {
        correctErrorTypes = true
        arguments {
            // 添加 Room schema export directory
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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
    //implementation(libs.androidx.compose.material3) // 使用 Material3

    // 使用 Material 而不是 Material3
    implementation("androidx.compose.material:material:1.4.0") // 或者你需要的具体版本

    // Compose UI
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
    implementation("androidx.compose.ui:ui-graphics:1.4.0")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.7.0")

    // Room 依赖
    implementation("androidx.room:room-runtime:2.6.1") // 使用最新版本
    kapt("androidx.room:room-compiler:2.6.1") // 使用 kapt 处理 Room 注解
    implementation("androidx.room:room-ktx:2.6.1") // 使用最新版本

    // ConfideActivity
    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("androidx.core:core-ktx:1.7.0")

    // comment display
    implementation("com.google.code.gson:gson:2.8.9")


}
