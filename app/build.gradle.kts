plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // ✅ Добавляем Compose Compiler Plugin
}

android {
    namespace = "com.radiax.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radiax.app"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {  // ✅ Перемещено внутрь android {}
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0" // ✅ Обновлено до последней версии
    }
}

dependencies {
    // Основные зависимости AndroidX
    implementation("androidx.core:core-ktx:1.12.0") // Обновлено
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.8.0") // Обновлено

    // Retrofit и OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // Обновлено
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Обновлено

    // Jetpack Compose (обновлённый и согласованный стек)
    implementation("androidx.compose.ui:ui:1.5.3") // Обновлено
    implementation("androidx.compose.material:material:1.5.3") // Обновлено
    implementation("androidx.compose.material3:material3:1.2.0") // Оставлено без изменений
    implementation("androidx.compose.foundation:foundation:1.5.3") // Обновлено
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.3") // Обновлено
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2") // Обновлено
    implementation("androidx.activity:activity-compose:1.8.0") // Обновлено
    implementation("androidx.compose.material3:material3:1.2.0") // ✅ Material 3
    implementation("androidx.compose.ui:ui:1.5.3") // ✅ Jetpack Compose UI
    implementation("com.google.android.material:material:1.9.0")

    // Тестирование
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}