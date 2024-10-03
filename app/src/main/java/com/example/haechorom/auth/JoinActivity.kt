package com.example.haechorom.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haechorom.R
import com.example.haechorom.MainActivity

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

        // 중복 확인 버튼 클릭 리스너
        checkDuplicateBtn.setOnClickListener {
            val enteredId = idArea.text.toString()

            if (enteredId.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해 주세요.", Toast.LENGTH_LONG).show()
            } else {
                // 실제 서버와 연결 후 이 부분에서 아이디 중복 체크
                // 현재는 중복이 없다는 토스트 메시지만 출력
                Toast.makeText(this, "중복된 아이디가 없습니다.", Toast.LENGTH_LONG).show()
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

            // 서버와 연결 전이므로 입력된 데이터를 사용하지 않고 바로 MainActivity로 이동
            val intent = Intent(this@JoinActivity, MainActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("phone", phone)
            intent.putExtra("email", email)
            intent.putExtra("userId", userId)
            intent.putExtra("mode", modeSpinner.selectedItem.toString()) // 선택된 모드를 전달
            startActivity(intent)

            // 가입 완료 메시지
            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()

            // JoinActivity 종료
            finish()
        }
    }
}
