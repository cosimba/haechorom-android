package com.example.haechorom.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.haechorom.MainActivity
import com.example.haechorom.R
import com.example.haechorom.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginBtn.setOnClickListener { // 로그인 버튼 눌렀을 때 로그인 창으로 화면 전환
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.joinBtn.setOnClickListener { // 회원가입 버튼 눌렀을 떄 회원가입 창으로 화면 전환
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}
