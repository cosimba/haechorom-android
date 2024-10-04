package com.example.haechorom.auth.api

// 로그인 응답 으로 받을 데이터 클래스
data class LoginResponse(
    val token: String,  // JWT 토큰을 받을 예정
    val role: String    // 사용자 의 역할 (예: 조사자, 청소자 등)
)
