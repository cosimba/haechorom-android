package com.example.haechorom.mode3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haechorom.MainActivity
import com.example.haechorom.R

class Menu3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu3)

        // 로그아웃 버튼 클릭 시 MainActivity로 이동
        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            // 로그아웃 처리 후 MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            // 필요시 이전 액티비티 스택을 모두 비움
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        // 뒤로 가기 버튼 클릭 시 이전 액티비티로 돌아가기
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료, Mode3Activity로 돌아감
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
