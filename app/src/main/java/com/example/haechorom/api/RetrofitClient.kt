package com.example.haechorom.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit instance 생성
object RetrofitClient {

    private const val BASE_URL = "http://34.47.71.166:8080" // 여기에 서버 주소를 적습니다.

//    // 안드로이드 클라이언트에서 서버와 연결 => Retrofit 을 사용해 서버와 통신
//    // Retrofit -> gradle 에 설정 필요
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환을 위한 GSON 사용
            .build()
    }

//    // Gson -> gradle 에 설정 필요
//    implementation("com.google.code.gson:gson:2.8.8")
//    implementation ("com.squareup.okhttp3:okhttp:4.9.1")

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java) // ApiService 인터페이스 생성
    }
}