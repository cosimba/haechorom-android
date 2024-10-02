package com.example.haechorom.mode2

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.haechorom.R
import com.example.haechorom.databinding.ActivityMode2Binding
import com.example.haechorom.mode1.Invest1Activity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class Mode2Activity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private lateinit var binding: ActivityMode2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode2)

        // MapView 초기화
        mapView = findViewById(R.id.map_view)

        // ViewBinding 초기화
        binding = ActivityMode2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 클릭 시 Invest1Activity로 이동
        binding.button.setOnClickListener {
            val intent = Intent(this@Mode2Activity, Clean2Activity::class.java)
            startActivity(intent)
        }

        binding.mapView.start(object : KakaoMapReadyCallback() {
            val marketLatitude = 35.1796
            val marketLongitude = 129.0756
            val market = LatLng.from(marketLatitude, marketLongitude)

            override fun onMapReady(kakaoMap: KakaoMap) {
                // 스타일 지정. LabelStyle.from()안에 원하는 이미지 넣기
                val style = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.location_pin)))
                // 라벨 옵션 지정. 위경도 와 스타일 넣기
                val options = LabelOptions.from(LatLng.from(market!!.latitude, market!!.longitude)).setStyles(style)
                // 레이어 가져 오기
                val layer = kakaoMap.labelManager?.layer
                // 레이어에 라벨 추가
                layer?.addLabel(options)
            }
            override fun getPosition(): LatLng {
                market ?: return super.getPosition()

                // 카메라 위치 지정
                return LatLng.from(market!!.latitude, market!!.longitude)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e(TAG, "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e(TAG, "onMapError", error)
            }

        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@Mode2Activity.kakaoMap = kakaoMap
                Log.e(TAG, "onMapReady")
            }
        })
    }

    // 뒤로가기를 눌렀을 때 앱 종료
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // 모든 액티비티 종료 (앱 종료)
    }
}