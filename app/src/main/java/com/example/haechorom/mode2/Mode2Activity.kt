package com.example.haechorom.mode2

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import android.location.LocationManager
import android.content.Context
import android.widget.Toast
import com.example.haechorom.api.RetrofitClient
import com.example.haechorom.api.dto.response.CleanPostResponse
import com.example.haechorom.databinding.ActivityMode2Binding
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Mode2Activity : AppCompatActivity() {

    // 바인딩 객체 생성
    private lateinit var binding: ActivityMode2Binding
    private var kakaoMap: KakaoMap? = null
    private var locationLabel: Label? = null  // 내 위치를 나타내는 라벨
    private val cleanLabels: MutableList<Label?> = mutableListOf()

    // 클릭한 위치의 위도와 경도
    private var clickedLatitude: Double = 0.0
    private var clickedLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 설정
        binding = ActivityMode2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            Log.d("Mode2Activity", "보내는 위도: $clickedLatitude, 경도: $clickedLongitude")

            val intent = Intent(this@Mode2Activity, Clean2Activity::class.java)
            intent.putExtra("latitude", clickedLatitude)
            intent.putExtra("longitude", clickedLongitude)
            startActivity(intent)
        }

        // 위치 권한 요청
        checkLocationPermission()

        // KakaoMapReadyCallback 설정
        binding.mapView.start(object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@Mode2Activity.kakaoMap = kakaoMap

                // 내 위치를 기반으로 카메라를 이동시킴
                moveToCurrentLocation(kakaoMap)

                // 줌 레벨을 조정해 초기 줌이 너무 크지 않도록 설정
                val cameraUpdate = CameraUpdateFactory.zoomTo(15) // 적절한 줌 레벨로 설정
                kakaoMap.moveCamera(cameraUpdate)

                // 조사 기록을 불러와서 지도에 표시
                loadCleanRecords()
            }
        })
    }

    // 조사 기록 가져오기
    private fun loadCleanRecords() {
        RetrofitClient.apiService.getAllCleanRecords().enqueue(object :
            Callback<List<CleanPostResponse>> {
            override fun onResponse(call: Call<List<CleanPostResponse>>, response: Response<List<CleanPostResponse>>) {
                if (response.isSuccessful) {
                    val cleanList = response.body()
                    Log.d("청소 내역", cleanList.toString())
                    cleanList?.let {
                        addMarkersToMap(it)
                    }
                } else {
                    Log.d("청소 내역", "응답 실패: ${response.code()}")
                    Toast.makeText(this@Mode2Activity, "청소 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CleanPostResponse>>, t: Throwable) {
                Log.e("청소 내역", "에러: ${t.message}")
                Toast.makeText(this@Mode2Activity, "청소 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 라벨을 지도에 추가하는 함수
    private fun addMarkersToMap(cleanList: List<CleanPostResponse>) {
        cleanList.forEach { clean ->
            val position = LatLng.from(clean.lat, clean.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.red_pin))

            // 라벨에 josaId를 텍스트로 설정
            val labelOptions = LabelOptions.from(position)
                .setStyles(styles)
                .setTexts(clean.id.toString()) // josaId를 텍스트로 추가

            val labelLayer = kakaoMap?.getLabelManager()?.getLayer()

            labelLayer?.let {
                val label = it.addLabel(labelOptions)
                label?.let { addedLabel ->
                    cleanLabels.add(addedLabel)
                    Log.d("라벨 추가", "라벨이 추가되었습니다: $clean")
                }
            }
        }

        // 라벨 클릭 리스너를 설정
        kakaoMap?.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kakaoMap: KakaoMap, layer: LabelLayer, clickedLabel: Label) {
                // 라벨의 텍스트(cleanId)를 가져옴
                val cleanId = clickedLabel.texts?.get(0)?.toLongOrNull()

                // 해당하는 조사 데이터를 찾아 모달을 띄움
                val clickedClean = cleanList.find { it.id == cleanId }
                clickedClean?.let {
                    showModal(it)
                }
            }
        })
        Log.d("Listener", "라벨 클릭 리스너가 등록되었습니다.")
    }

    // 모달 창을 띄우는 함수
    private fun showModal(clean: CleanPostResponse) {
        val dialog = CustomCleanDialogFragment.newInstance(clean)
        dialog.show(supportFragmentManager, "JosaModal")
        Log.d("모달 확인", "모달을 띄웁니다: ${clean.cleanName}") // 테스트
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

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            kakaoMap?.let { moveToCurrentLocation(it) } // 권한 승인 시 내 위치로 이동
        }
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
                updateLatLngTextView(myLocation)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLatLngTextView(location: LatLng) {
//        Log.d("LatLngUpdate", "TextView에 출력 - 위도: ${location.latitude}, 경도: ${location.longitude}")
//        val formattedLatitude = String.format("%.4f", location.latitude)
//        val formattedLongitude = String.format("%.4f", location.longitude)
//
//        // 바인딩을 통해 TextView에 접근
//        binding.latLngText.text = "위도: $formattedLatitude\n경도: $formattedLongitude"

        // clickedLatitude와 clickedLongitude에 현재 위치 값 복사
        clickedLatitude = location.latitude
        clickedLongitude = location.longitude
    }

    // 라벨을 내 위치에 추가하는 함수
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

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}

