package com.example.haechorom.auth.api

// 로그인 요청 시 사용할 데이터 클래스
data class LoginRequest(
    val userId: String,
    val password: String
)
