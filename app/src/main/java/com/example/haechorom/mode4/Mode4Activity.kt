package com.example.haechorom.mode4

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.haechorom.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.example.haechorom.databinding.ActivityMode4Binding
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class Mode4Activity : AppCompatActivity() {

    // 바인딩 객체 생성
    private lateinit var binding: ActivityMode4Binding
    private var kakaoMap: KakaoMap? = null
    private var locationLabel: Label? = null  // 내 위치를 나타내는 라벨

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

                // * 추가적인 핀 찍기 예시
                addPinToLocation(kakaoMap, 35.168000, 129.130000) // 예시 위치
                addPinToLocation(kakaoMap, 35.165000, 129.133000) // 또 다른 예시 위치
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

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            kakaoMap?.let { moveToFixedLocation(it) } // 권한 승인 시 고정 위치로 이동
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

        // 고정된 위치에 라벨을 추가
        addLabelToLocation(kakaoMap, myLocation)
    }

    // 라벨을 고정된 위치에 추가하는 함수
    private fun addLabelToLocation(kakaoMap: KakaoMap, location: LatLng) {
        // 기존에 라벨이 있으면 제거
        locationLabel?.remove()

        // LabelStyles 설정 - 단순한 아이콘 스타일을 적용
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.my_location_icon))  // my_location_icon: 위치 아이콘 리소스

        // 새로운 라벨 생성
        val labelOptions = LabelOptions.from(location)
            .setStyles(styles)  // 라벨 스타일 설정

        // LabelLayer에서 레이어 가져오기
        val labelLayer = kakaoMap.getLabelManager()?.getLayer()

        // 라벨 추가
        val label = labelLayer?.addLabel(labelOptions)

        // 새 라벨을 저장
        locationLabel = label
    }

    // 특정 위도와 경도로 핀 추가하는 함수
    private fun addPinToLocation(kakaoMap: KakaoMap, latitude: Double, longitude: Double) {
        val location = LatLng.from(latitude, longitude)

        // LabelStyles 설정 - 단순한 아이콘 스타일을 적용
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.blue_pin))  // pin_icon: 핀 아이콘 리소스

        // 새로운 라벨 생성
        val labelOptions = LabelOptions.from(location)
            .setStyles(styles)  // 라벨 스타일 설정

        // LabelLayer에서 레이어 가져오기
        val labelLayer = kakaoMap.getLabelManager()?.getLayer()

        // 라벨 추가
        labelLayer?.addLabel(labelOptions)
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