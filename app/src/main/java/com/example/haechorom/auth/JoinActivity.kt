package com.example.haechorom.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haechorom.R
import com.example.haechorom.MainActivity
import android.widget.Toast
import com.example.haechorom.auth.api.RetrofitClient
import com.example.haechorom.auth.api.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI 컴포넌트 연결
        val nameArea = findViewById<EditText>(R.id.nameArea)
        val numArea = findViewById<EditText>(R.id.numArea)
        val emailArea = findViewById<EditText>(R.id.emailArea)
        val idArea = findViewById<EditText>(R.id.idArea)
        val passwordArea = findViewById<EditText>(R.id.passwordArea)
        val passwordCheckArea = findViewById<EditText>(R.id.passwordCheckArea)
        val modeSpinner = findViewById<Spinner>(R.id.modeSpinner)
        val joinBtn = findViewById<Button>(R.id.joinBtn)
        val checkDuplicateBtn = findViewById<Button>(R.id.button) // 중복 확인 버튼

        // 스피너에 커스텀 레이아웃 적용
        ArrayAdapter.createFromResource(
            this,
            R.array.modes_array,
            R.layout.custom_spinner_item // 커스텀 레이아웃 적용
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.custom_spinner_item)
            modeSpinner.adapter = adapter
        }

        // 아이디 중복 확인 버튼 클릭 리스너
        checkDuplicateBtn.setOnClickListener {
            val enteredId = idArea.text.toString()

            if (enteredId.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해 주세요.", Toast.LENGTH_LONG).show()
            } else {
                checkUserIdDuplicate(enteredId) // 중복 확인 함수 호출
            }
        }

        // 회원가입 버튼 클릭 리스너
        joinBtn.setOnClickListener {
            val name = nameArea.text.toString()
            val phone = numArea.text.toString()
            val email = emailArea.text.toString()
            val userId = idArea.text.toString()
            val password = passwordArea.text.toString()
            val passwordCheck = passwordCheckArea.text.toString()
            val selectedMode = modeSpinner.selectedItemPosition // 스피너에서 선택한 모드의 인덱스 값
            val pin = "설정 안 함"

            // 간단한 입력 필드 확인
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || userId.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 비밀번호 일치 확인
            if (password != passwordCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 모드 선택 여부 확인 (1번 항목: "모드를 선택해 주세요."인 경우)
            if (selectedMode == 0) {
                Toast.makeText(this, "모드를 선택해 주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 서버 통신 구현
            val role = User.getRoleFromInt(selectedMode + 1) // 역할 변환 (조사자, 청소자 등)

            // User 객체 생성
            val user = User(userId, password, name, email, phone, pin, role)

//            // 서버와 연결 전이므로 입력된 데이터를 사용하지 않고 바로 MainActivity로 이동
//            val intent = Intent(this@JoinActivity, MainActivity::class.java)
//            intent.putExtra("name", name)
//            intent.putExtra("phone", phone)
//            intent.putExtra("email", email)
//            intent.putExtra("userId", userId)
//            intent.putExtra("mode", modeSpinner.selectedItem.toString()) // 선택된 모드를 전달
//            startActivity(intent)

            // 테스트
            val gson = Gson()
            val jsonUser = gson.toJson(user)
            Log.d("User JSON", jsonUser)

            // 서버로 회원가입 요청
            RetrofitClient.apiService.registerUser(user).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                    if (response.isSuccessful) {
                        Toast.makeText(this@JoinActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        finish() // 회원가입 후 화면 종료
                    } else {
                        Toast.makeText(this@JoinActivity, "회원가입 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@JoinActivity, "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })

            // 가입 완료 메시지
            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()

            // JoinActivity 종료
            finish()
        }
    }

    // 아이디 중복 확인 요청을 보내는 함수
    private fun checkUserIdDuplicate(userId: String) {
        RetrofitClient.apiService.checkUserIdDuplicate(userId).enqueue(object : Callback<Boolean> {
            // 서버로부터 정상적인 응답을 받았을 때 호출
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) { // HTTP 상태 코드가 200~299인 경우에 true
                    val isDuplicate = response.body() ?: false
                    Log.d("JoinActivity", "중복 확인 요청 성공: ${response.body()}") // 중복 확인 요청 성공 시 로그 출력
                    if (isDuplicate) {
                        Toast.makeText(this@JoinActivity, "이미 사용 중인 아이디입니다.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@JoinActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.d("JoinActivity", "중복 확인 실패: 서버 응답 에러") // 서버 응답 실패 시 로그 출력
                    Toast.makeText(this@JoinActivity, "중복 확인 실패: 서버 오류", Toast.LENGTH_LONG).show()
                }
            }

            // 네트워크 오류나 서버와의 연결 실패 등으로 요청이 실패했을 때 호출
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d("JoinActivity", "중복 확인 요청 실패: ${t.message}") // 네트워크 요청 실패 시 로그 출력
                Toast.makeText(this@JoinActivity, "서버와의 연결 실패", Toast.LENGTH_LONG).show()
            }


        })

    }
}
