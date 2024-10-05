package com.example.haechorom.api

import com.example.haechorom.api.dto.request.LoginRequest
import com.example.haechorom.api.dto.response.CleanPostResponse
import com.example.haechorom.api.dto.response.CollectPostResponse
import com.example.haechorom.api.dto.response.JosaPostResponse
import com.example.haechorom.api.dto.response.LoginResponse
import com.example.haechorom.api.entity.User
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

    // 조사 모든 기록 조회 API
    @GET("/josa/api/v1/search/all")
    fun getAllJosaRecords(): Call<List<JosaPostResponse>>

    // 청소가 안끝난 청소 모드 조회 API
    @GET("/clean/api/v1/view/cleanyet")
    fun getAllCleanRecords(): Call<List<CleanPostResponse>>

    // 청소가 끝난 청소 모드 조회 API => 운전자 모드에 사용
    @GET("/clean/api/v1/view/cleanfin")
    fun getCleanFinished(): Call<List<CollectPostResponse>>
}