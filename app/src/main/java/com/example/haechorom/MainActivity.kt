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
import com.example.haechorom.auth.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.haechorom.auth.api.LoginRequest
import com.example.haechorom.auth.api.LoginResponse

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

//        var keyHash = Utility.getKeyHash(this)
//        Log.d("키 해시 값", keyHash)

        super.onCreate(savedInstanceState)

        // DataBinding 설정
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

//        // 스피너에 모드 배열 연결
//        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
//            this, R.array.modes_array, R.layout.spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.modeSpinner.adapter = adapter

        // 로그인 버튼 클릭 리스너
        binding.buttonLogin.setOnClickListener {
            val userId = binding.editTextId.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 로그인 요청
            val loginRequest = LoginRequest(userId, password)
            RetrofitClient.apiService.loginUser(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val loginResponse = response.body()!!
                            val token = loginResponse.token
                            val role = loginResponse.role

                            // JWT 토큰을 SharedPreferences 에 저장
                            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            editor.putString("token", token)  // 토큰 저장
                            editor.apply()

                            // 역할에 맞는 화면 으로 이동
                            val intent = when (role) {
                                "조사자" -> Intent(this@MainActivity, Mode1Activity::class.java)
                                "청소자" -> Intent(this@MainActivity, Mode2Activity::class.java)
                                "관리자" -> Intent(this@MainActivity, Mode3Activity::class.java)
                                "운송자" -> Intent(this@MainActivity, Mode4Activity::class.java)
                                else -> Intent(this@MainActivity, MainActivity::class.java)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "잘못된 아이디 또는 비밀번호입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "서버 오류", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // 회원 가입 화면 이동
        binding.textView.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}
