package com.example.haechorom.auth.api

import retrofit2.Call
import retrofit2.http.*

// server 통신을 위해 ApiService 라는 Retrofit interface 를 정의
interface ApiService {

    // 회원가입 API
    @POST("/user/auth/register")
    fun registerUser(@Body user: User): Call<Void> // User 객체를 전달 받아 서버에 POST 요청

    // 아이디 중복 체크 API
    @GET("/user/auth/check/{userId}")
    fun checkUserIdDuplicate(@Path("userId") userId: String): Call<Boolean>

    // 로그인 API
    @POST("/user/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

}