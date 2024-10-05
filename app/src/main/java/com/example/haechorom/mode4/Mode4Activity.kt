package com.example.haechorom.mode4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.haechorom.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.example.haechorom.databinding.ActivityMode4Binding
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class Mode4Activity : AppCompatActivity() {

    // 바인딩 객체 생성
    private lateinit var binding: ActivityMode4Binding
    private var kakaoMap: KakaoMap? = null
    private val pinLabels = mutableMapOf<LatLng, Label>() // 각 핀을 관리할 Map

    // 초록 핀들의 위치를 저장할 리스트
    private val greenPinLocations = mutableListOf<LatLng>()


    // 고정할 위도와 경도 값 (내 위치 임시 설정)
    private val fixedLatitude = 35.1678916
    private val fixedLongitude = 129.134145

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 설정
        binding = ActivityMode4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 위치 권한 요청
        checkLocationPermission()

        // KakaoMapReadyCallback 설정
        binding.mapView.start(object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@Mode4Activity.kakaoMap = kakaoMap

                // 초기 위치를 고정된 위도와 경도로 설정
                moveToFixedLocation(kakaoMap)

                // 파란 핀 추가
                addPinToLocation(kakaoMap, 35.168000, 129.130000) // 예시 위치 1
                addPinToLocation(kakaoMap, 35.165000, 129.133000) // 예시 위치 2

                // 클릭 이벤트 설정
                setLabelClickListener(kakaoMap)
            }
        })
    }

    // 위치 권한 확인
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            // 권한이 이미 허용된 경우 즉시 위치를 요청
            kakaoMap?.let { moveToFixedLocation(it) }
        }
    }

    // 고정된 위치로 카메라 이동
    private fun moveToFixedLocation(kakaoMap: KakaoMap) {
        val myLocation = LatLng.from(fixedLatitude, fixedLongitude)
        val cameraPosition = CameraPosition.from(
            myLocation.latitude,
            myLocation.longitude,
            10, // 적당한 줌 레벨
            0.0, // 기울기 없음
            0.0, // 회전 없음
            0.0 // 높이
        )
        kakaoMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // 특정 위도와 경도로 파란 핀 추가하는 함수
    private fun addPinToLocation(kakaoMap: KakaoMap, latitude: Double, longitude: Double) {
        val location = LatLng.from(latitude, longitude)
        val bluePinStyle = LabelStyles.from(LabelStyle.from(R.drawable.blue_pin)) // 파란 핀 스타일
        val labelOptions = LabelOptions.from(location).setStyles(bluePinStyle)
        val labelLayer = kakaoMap.getLabelManager()?.getLayer()
        val pinLabel = labelLayer?.addLabel(labelOptions)

        // 추가한 파란 핀을 저장
        pinLabel?.let {
            pinLabels[location] = it // 위치와 라벨을 매핑해 저장
        }
    }

    // 라벨 클릭 리스너 설정
    private fun setLabelClickListener(kakaoMap: KakaoMap) {
        kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kakaoMap: KakaoMap, layer: LabelLayer, label: Label) {
                // 클릭된 라벨의 위치를 저장된 값에서 찾음
                val labelLocation = pinLabels.entries.firstOrNull { it.value == label }?.key

                labelLocation?.let {
                    // 기존 라벨 삭제
                    pinLabels[labelLocation]?.remove()

                    // 초록 핀 스타일로 새로운 라벨 추가
                    val greenPinStyle = LabelStyles.from(LabelStyle.from(R.drawable.green_pin)) // 초록 핀 스타일
                    val labelOptions = LabelOptions.from(it).setStyles(greenPinStyle)
                    val labelLayer = kakaoMap.getLabelManager()?.getLayer()

                    // 새로 추가된 초록 핀 저장
                    val newLabel: Label? = labelLayer?.addLabel(labelOptions)

                    if (newLabel != null) {
                        pinLabels[labelLocation] = newLabel
                        Toast.makeText(this@Mode4Activity, "파란 핀이 초록 핀으로 바뀌었습니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Mode4Activity, "핀 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                    Toast.makeText(this@Mode4Activity, "파란 핀이 초록 핀으로 바뀌었습니다!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }



    override fun onResume() {
        super.onResume()
        binding.mapView.resume() // 지도 라이프사이클 resume() 호출
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause() // 지도 라이프사이클 pause() 호출
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.finish() // 명시적으로 지도 종료
    }

    // 뒤로가기를 눌렀을 때 앱 종료
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // 모든 액티비티 종료 (앱 종료)
    }
}
