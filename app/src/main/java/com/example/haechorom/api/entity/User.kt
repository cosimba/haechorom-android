package com.example.haechorom.api.entity

data class User(
    val userId: String,
    val password: String,
    val name: String,
    val email: String,
    val phNum: String,
    val pin: String? = null,  // 핀 번호는 null 허용
    val role: String  // 문자열 로 역할을 보냄 ("조사자", "청소자" 등)
) {
    companion object {
        fun getRoleFromInt(roleInt: Int): String {
            return when (roleInt) {
                2 -> "조사자"
                3 -> "청소자"
                4 -> "관리자"
                5 -> "운송자"
                else -> "알 수 없음"
            }
        }
//        fun getRoleFromString(roleString: String): Int {
//            return when (roleString) {
//                "조사자" -> 2
//                "청소자" -> 3
//                "관리자" -> 4
//                "운송자" -> 5
//                else -> 0
//            }
//        }
    }
}
