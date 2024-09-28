plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.haechorom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.haechorom"
        minSdk = 34
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

    dataBinding { // dataBinding
        enable = true
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

//    // 카카오 SDK 모듈
//    implementation("com.kakao.sdk:v2-all:2.20.6") // 전체 모듈 설치
//    implementation("com.kakao.sdk:v2-user:2.20.6") // 카카오 로그인 API 모듈
//    implementation("com.kakao.sdk:v2-share:2.20.6") // 카카오톡 공유 API 모듈
//    implementation("com.kakao.sdk:v2-talk:2.20.6") // 카카오톡 채널, 카카오톡 소셜, 카카오톡 메시지 API 모듈
//    implementation("com.kakao.sdk:v2-friend:2.20.6") // 피커 API 모듈
//    implementation("com.kakao.sdk:v2-navi:2.20.6") // 카카오내비 API 모듈
//    implementation("com.kakao.sdk:v2-cert:2.20.6") // 카카오톡 인증 서비스 API 모듈
//
//    // 카카오 지도 SDK 추가
//    implementation("net.daum.mf.map:native-sdk:1.4.0")  // 최신 버전 확인 후 사용
}
