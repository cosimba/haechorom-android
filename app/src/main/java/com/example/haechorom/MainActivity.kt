package com.example.haechorom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.haechorom.mode1.Mode1Activity
import com.example.haechorom.mode2.Mode2Activity
import com.example.haechorom.mode3.Mode3Activity
import com.example.haechorom.mode4.Mode4Activity

    // * 이 project 는 MainActivity 가 로그인 화면 입니다 *

class MainActivity : AppCompatActivity() {

    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var spinnerMode: Spinner
    private lateinit var buttonLogin: Button

    // 임시 아이디와 비밀번호
    private val tempId = "user123"
    private val tempPassword = "password123"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View 연결
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        spinnerMode = findViewById(R.id.modeSpinner)
        buttonLogin = findViewById(R.id.buttonLogin)

        // 스피너에 모드 배열 연결
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this, R.array.modes_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMode.adapter = adapter

        // 로그인 버튼 클릭 리스너
        buttonLogin.setOnClickListener {
            val id = editTextId.text.toString()
            val password = editTextPassword.text.toString()
            val mode = spinnerMode.selectedItemPosition + 1 // 모드 1~4 선택

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else if (id == tempId && password == tempPassword) {
                // 로그인 성공, 모드에 따라 다른 메인 화면으로 이동
                val intent: Intent = when (mode) {
                    1 -> Intent(this, Mode1Activity::class.java)
                    2 -> Intent(this, Mode2Activity::class.java)
                    3 -> Intent(this, Mode3Activity::class.java)
                    4 -> Intent(this, Mode4Activity::class.java)
                    else -> Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish() // MainActivity 종료
            } else {
                // 로그인 실패
                Toast.makeText(this, "잘못된 아이디 또는 비밀번호입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
