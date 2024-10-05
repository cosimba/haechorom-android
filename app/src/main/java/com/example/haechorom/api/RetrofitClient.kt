package com.example.haechorom.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit instance 생성
object RetrofitClient {

    private const val BASE_URL = "http://34.47.71.166:8080" // 여기에 서버 주소를 적습니다.

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환을 위한 GSON 사용
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java) // ApiService 인터페이스 생성
    }
}