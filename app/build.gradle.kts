plugins {
    // 플러그인 설정
    id("com.android.application") // Kotlin Android 플러그인
    id("org.jetbrains.kotlin.android") // Kotlin Android 플러그인
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.ryun.ishare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ryun.ishare"
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // ViewModel injection용
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ✅ Compose BOM 사용 (최신 안정 버전으로 교체)
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    //implementation(platform(coreLibs.androidx.compose.bom))

    // ✅ material3만 명시적 버전으로 분리 (BOM 영향 안 받게)
    implementation("androidx.compose.material3:material3")
    //implementation(coreLibs.androidx.compose.material3)

// ✅ 기본 Compose 세팅
    implementation("androidx.core:core-ktx:1.12.0") // ✅ 유지
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.8.2") // ✅ 유지

    // ✅ Compose UI 관련 (버전 생략 → BOM이 관리)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout") // Layout 관련 (Row, Column 등)
    implementation("androidx.compose.ui:ui-text")
    implementation("androidx.compose.runtime:runtime") // 기본 runtime
    implementation("androidx.compose.runtime:runtime-livedata") // LiveData 연동
    implementation("androidx.compose.ui:ui-unit")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // **Navigation Compose 의존성 추가**
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // ✅ 테스트
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // 디버그
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
