package com.example.haechorom.mode3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haechorom.R

class Mode3Activity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_mode3)

        val year1 = findViewById<EditText>(R.id.year_1)
        val month1 = findViewById<EditText>(R.id.month_1)
        val day1 = findViewById<EditText>(R.id.date_1)

        val year2 = findViewById<EditText>(R.id.year_2)
        val month2 = findViewById<EditText>(R.id.month_2)
        val day2 = findViewById<EditText>(R.id.date_2)

        // 첫 번째 필드에서 자동 이동 기능
        setupAutoMove(year1, month1, 4)
        setupAutoMove(month1, day1, 2)
        setupAutoMove(day1, year2, 2)

        // 두 번째 필드도 자동으로 입력되도록 설정
        setupAutoMove(year2, month2, 4)
        setupAutoMove(month2, day2, 2)

        // 날짜 검증 로직
        day2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2) {
                    validateDates(year1, month1, day1, year2, month2, day2)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 메뉴 텍스트뷰 클릭 시 MenuActivity로 이동
        val menuTextView = findViewById<TextView>(R.id.menu)
        menuTextView.setOnClickListener {
            val intent = Intent(this, Menu3Activity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 자동 필드 이동 설정 함수
    private fun setupAutoMove(currentField: EditText, nextField: EditText?, limit: Int) {
        currentField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == limit && nextField != null) {
                    nextField.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // 날짜 검증 함수 (두 번째 필드의 날짜가 첫 번째 필드의 날짜보다 이후여야 함)
    private fun validateDates(
        year1: EditText, month1: EditText, day1: EditText,
        year2: EditText, month2: EditText, day2: EditText
    ) {
        try {
            val startDate = "${year1.text}-${month1.text}-${day1.text}".toIntDate()
            val endDate = "${year2.text}-${month2.text}-${day2.text}".toIntDate()

            if (startDate >= endDate) {
                Toast.makeText(this, "두 번째 날짜는 첫 번째 날짜보다 나중이어야 합니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "날짜 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 날짜를 숫자로 변환하는 확장 함수
    private fun String.toIntDate(): Int {
        val parts = this.split("-")
        return parts[0].toInt() * 10000 + parts[1].toInt() * 100 + parts[2].toInt()
    }

    // 뒤로 가기를 눌렀을 때 앱 종료
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // 모든 액티비티 종료 (앱 종료)
    }
}
