package com.example.haechorom.mode1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.example.haechorom.R

class Mode1Activity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_mode1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // MapView 초기화
        mapView = findViewById(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 정상 종료 시 호출됨
            }

            override fun onMapError(error: Exception) {
                // 지도 API 사용 중 에러 발생 시 호출됨
                error.printStackTrace()  // 로그 출력 또는 에러 처리 로직 추가
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 성공 후, 지도 사용 가능 상태 시 호출됨
                // 필요 시 여기서 지도 관련 로직 추가 가능
            }
        })
    }



    override fun onResume() {
        super.onResume()
        mapView.resume()  // MapView의 resume 호출
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()  // MapView의 pause 호출
    }
}
