package com.example.haechorom.mode4

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.haechorom.R
import com.example.haechorom.api.RetrofitClient
import com.example.haechorom.api.dto.response.CollectPostResponse
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
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Mode4Activity : AppCompatActivity() {

    // 바인딩 객체 생성
    private lateinit var binding: ActivityMode4Binding
    private var kakaoMap: KakaoMap? = null
    private var locationLabel: Label? = null
    private val pinLabels = mutableMapOf<LatLng, Label>() // 각 핀을 관리할 Map

    // 초록 핀들의 위치를 저장할 리스트
    private val greenPinLocations = mutableListOf<LatLng>()

//    // 고정할 위도와 경도 값 (내 위치 임시 설정)
//    private val fixedLatitude = 35.1678916
//    private val fixedLongitude = 129.134145

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

                // 내 위치를 기반으로 카메라를 이동
                moveToCurrentLocation(kakaoMap)

                val cameraUpdate = CameraUpdateFactory.zoomTo(15)
                kakaoMap.moveCamera(cameraUpdate)

//                // 파란 핀 추가
//                addPinToLocation(kakaoMap, 35.168000, 129.130000) // 예시 위치 1
//                addPinToLocation(kakaoMap, 35.165000, 129.133000) // 예시 위치 2

                // 클릭 이벤트 설정
                setLabelClickListener(kakaoMap)

                // 운송 시작 버튼 클릭 시 초록 핀 연결
                binding.button.setOnClickListener {
                    connectGreenPins(kakaoMap)
                }

                // 청소 기록 불러와서 지도에 표시
                loadCleanRecords()
            }
        })
    }

    // 위치 권한 확인
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            // 권한이 이미 허용된 경우 즉시 위치를 요청
            kakaoMap?.let { moveToCurrentLocation(it) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            kakaoMap?.let { moveToCurrentLocation(it) }
        }
    }

    private fun addLabelToLocation(kakaoMap: KakaoMap, location: LatLng) {
        locationLabel?.remove()

        val styles = LabelStyles.from(LabelStyle.from(R.drawable.my_location_icon))
        val labelOptions = LabelOptions.from(location).setStyles(styles)
        val labelLayer = kakaoMap.getLabelManager()?.getLayer()

        val label = labelLayer?.addLabel(labelOptions)
        locationLabel = label
    }

    // 내 위치로 카메라 이동
    private fun moveToCurrentLocation(kakaoMap: KakaoMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            location?.let {
                val myLocation = LatLng.from(it.latitude, it.longitude)
                val cameraPosition = CameraPosition.from(
                    myLocation.latitude,
                    myLocation.longitude,
                    15,
                    0.0,
                    0.0,
                    0.0
                )
                kakaoMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                addLabelToLocation(kakaoMap, myLocation)
            }
        }
    }

    // 청소 기록 가져오기
    private fun loadCleanRecords() {
        RetrofitClient.apiService.getCleanFinished().enqueue(object : Callback<List<CollectPostResponse>> {
            override fun onResponse(call: Call<List<CollectPostResponse>>, response: Response<List<CollectPostResponse>>) {
                if (response.isSuccessful) {
                    val cleanList = response.body()
                    Log.d("청소 내역", cleanList.toString())
                    cleanList?.let {
                        addCleanMarkersToMap(it)
                    }
                } else {
                    Log.d("청소 내역", "응답 실패: ${response.code()}")
                    Toast.makeText(this@Mode4Activity, "청소 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CollectPostResponse>>, t: Throwable) {
                Log.e("청소 내역", "에러: ${t.message}")
                Toast.makeText(this@Mode4Activity, "청소 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 라벨을 지도에 추가하는 함수
    private fun addCleanMarkersToMap(cleanList: List<CollectPostResponse>) {
        cleanList.forEach { clean ->
            val position = LatLng.from(clean.lat, clean.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.blue_pin))

            // 라벨에 cleanId를 텍스트로 설정
            val labelOptions = LabelOptions.from(position)
                .setStyles(styles)
                .setTexts(clean.id.toString()) // cleanId를 텍스트로 추가

            val labelLayer = kakaoMap?.getLabelManager()?.getLayer()

            labelLayer?.let {
                val label = it.addLabel(labelOptions)
                label?.let { addedLabel ->
                    pinLabels[position] = addedLabel
                    Log.d("라벨 추가", "라벨이 추가되었습니다: $clean")
                }
            }
        }
    }


//    // 고정된 위치에 my_location_icon 라벨 추가
//    private fun moveToFixedLocation(kakaoMap: KakaoMap) {
//        val myLocation = LatLng.from(fixedLatitude, fixedLongitude)
//
//        // 라벨 스타일 설정
//        val labelStyle = LabelStyles.from(LabelStyle.from(R.drawable.my_location_icon)) // my_location_icon을 라벨로 설정
//
//        // 라벨 옵션 설정
//        val labelOptions = LabelOptions.from(myLocation)
//            .setStyles(labelStyle)
//
//        // LabelLayer에서 레이어 가져오기
//        val labelLayer = kakaoMap.getLabelManager()?.getLayer()
//
//        // 라벨 추가
//        labelLayer?.addLabel(labelOptions)
//
//        // 카메라 위치 이동
//        val cameraPosition = CameraPosition.from(
//            myLocation.latitude,
//            myLocation.longitude,
//            10, // 적당한 줌 레벨
//            0.0, // 기울기 없음
//            0.0, // 회전 없음
//            0.0 // 높이
//        )
//        kakaoMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//    }

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
                val labelLocation = pinLabels.entries.firstOrNull { it.value == label }?.key

                labelLocation?.let {
                    // 기존 라벨 삭제
                    pinLabels[labelLocation]?.remove()

                    // 초록 핀으로 변경
                    val greenPinStyle = LabelStyles.from(LabelStyle.from(R.drawable.green_pin))
                    val labelOptions = LabelOptions.from(it).setStyles(greenPinStyle)
                    val labelLayer = kakaoMap.getLabelManager()?.getLayer()
                    val newLabel = labelLayer?.addLabel(labelOptions)

                    newLabel?.let {
                        pinLabels[labelLocation] = it
                        greenPinLocations.add(labelLocation) // 초록 핀 리스트에 추가
                    }
                }
            }
        })
    }

    // 초록 핀을 my_location_icon에서부터 연결하는 함수
    private fun connectGreenPins(kakaoMap: KakaoMap) {
        if (greenPinLocations.size > 0) {  // 최소 하나의 초록 핀이 필요함
            // 라인 스타일 설정
            val stylesSet = RouteLineStylesSet.from("blueStyle",
                RouteLineStyles.from(RouteLineStyle.from(20f, 0xFF3E8EDE.toInt()))) // 두께 5f, 초록색 설정

            // Get current location
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                location?.let {
                    // 현재 위치를 myLocation으로 설정
                    val myLocation = LatLng.from(it.latitude, it.longitude)

                    // RouteLineSegment 생성 및 연결
                    val segments = mutableListOf<RouteLineSegment>()
                    var start = myLocation  // 현재 위치에서 시작

                    for (i in 0 until greenPinLocations.size) {
                        val end = greenPinLocations[i]
                        segments.add(RouteLineSegment.from(listOf(start, end)).setStyles(stylesSet.getStyles(0)))
                        start = end // 다음 핀을 시작점으로 설정
                    }

                    // RouteLineOptions 생성 및 추가
                    val routeLineOptions = RouteLineOptions.from(segments)
                        .setStylesSet(stylesSet)

                    kakaoMap.getRouteLineManager()?.getLayer()?.addRouteLine(routeLineOptions)
                } ?: run {
                    Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "적어도 하나 이상의 초록 핀이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
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
