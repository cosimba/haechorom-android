package com.example.haechorom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.haechorom.auth.JoinActivity
import com.example.haechorom.databinding.ActivityMainBinding
import com.example.haechorom.mode1.Mode1Activity
import com.example.haechorom.mode2.Mode2Activity
import com.example.haechorom.mode3.Mode3Activity
import com.example.haechorom.mode4.Mode4Activity
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private val tempId = "user123"
    private val tempPassword = "password123"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        var keyHash = Utility.getKeyHash(this)
        Log.d("키 해시 값", keyHash)

        super.onCreate(savedInstanceState)

        // DataBinding 설정
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 스피너에 모드 배열 연결
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this, R.array.modes_array, R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.modeSpinner.adapter = adapter

        // 로그인 버튼 클릭 리스너
        binding.buttonLogin.setOnClickListener {
            val id = binding.editTextId.text.toString()
            val password = binding.editTextPassword.text.toString()
            val mode = binding.modeSpinner.selectedItemPosition + 1 // 모드 2~5 선택

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else if (id == tempId && password == tempPassword) {
                val intent: Intent = when (mode) {
                    2 -> Intent(this, Mode1Activity::class.java)
                    3 -> Intent(this, Mode2Activity::class.java)
                    4 -> Intent(this, Mode3Activity::class.java)
                    5 -> Intent(this, Mode4Activity::class.java)
                    else -> Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "잘못된 아이디 또는 비밀번호입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 회원가입 텍스트 클릭 리스너
        binding.textView.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}
