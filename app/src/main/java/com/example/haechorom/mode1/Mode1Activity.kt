package com.example.haechorom.mode1

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
import com.example.haechorom.databinding.ActivityMode1Binding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import android.location.LocationManager
import android.content.Context
import android.widget.Toast
import com.example.haechorom.CustomDialogFragment
import com.example.haechorom.api.RetrofitClient
import com.example.haechorom.api.dto.response.JosaPostResponse
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Mode1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityMode1Binding
    private var kakaoMap: KakaoMap? = null
    private var locationLabel: Label? = null
    private val josaLabels: MutableList<Label?> = mutableListOf()

    private var clickedLatitude: Double = 0.0
    private var clickedLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMode1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            Log.d("Mode1Activity", "보내는 위도: $clickedLatitude, 경도: $clickedLongitude")
            val intent = Intent(this@Mode1Activity, Invest1Activity::class.java)
            intent.putExtra("latitude", clickedLatitude)
            intent.putExtra("longitude", clickedLongitude)
            startActivity(intent)
        }

        checkLocationPermission()

        // KakaoMapReadyCallback 설정
        binding.mapView.start(object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@Mode1Activity.kakaoMap = kakaoMap

                // 내 위치를 기반으로 카메라를 이동
                moveToCurrentLocation(kakaoMap)

                val cameraUpdate = CameraUpdateFactory.zoomTo(15)
                kakaoMap.moveCamera(cameraUpdate)

                // 조사 기록을 불러와서 지도에 표시
                loadJosaRecords()
            }
        })
    }

    // 조사 기록 가져오기
    private fun loadJosaRecords() {
        RetrofitClient.apiService.getAllJosaRecords().enqueue(object : Callback<List<JosaPostResponse>> {
            override fun onResponse(call: Call<List<JosaPostResponse>>, response: Response<List<JosaPostResponse>>) {
                if (response.isSuccessful) {
                    val josaList = response.body()
                    Log.d("조사 내역", josaList.toString())
                    josaList?.let {
                        addMarkersToMap(it)
                    }
                } else {
                    Log.d("조사 내역", "응답 실패: ${response.code()}")
                    Toast.makeText(this@Mode1Activity, "조사 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<JosaPostResponse>>, t: Throwable) {
                Log.e("조사 내역", "에러: ${t.message}")
                Toast.makeText(this@Mode1Activity, "조사 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 라벨을 지도에 추가하는 함수
    private fun addMarkersToMap(josaList: List<JosaPostResponse>) {
        josaList.forEach { josa ->
            val position = LatLng.from(josa.lat, josa.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.red_pin))

            // 라벨에 josaId를 텍스트로 설정
            val labelOptions = LabelOptions.from(position)
                .setStyles(styles)
                .setTexts(josa.id.toString()) // josaId를 텍스트로 추가

            val labelLayer = kakaoMap?.getLabelManager()?.getLayer()

            labelLayer?.let {
                val label = it.addLabel(labelOptions)
                label?.let { addedLabel ->
                    josaLabels.add(addedLabel)
                    Log.d("라벨 추가", "라벨이 추가되었습니다: $josa")
                }
            }
        }

        // 라벨 클릭 리스너를 설정
        kakaoMap?.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kakaoMap: KakaoMap, layer: LabelLayer, clickedLabel: Label) {
                // 라벨의 텍스트(josaId)를 가져옴
                val josaId = clickedLabel.texts?.get(0)?.toLongOrNull()

                // 해당하는 조사 데이터를 찾아 모달을 띄움
                val clickedJosa = josaList.find { it.id == josaId }
                clickedJosa?.let {
                    showModal(it)
                }
            }
        })
        Log.d("Listener", "라벨 클릭 리스너가 등록되었습니다.")
    }

    // 모달 창을 띄우는 함수
    private fun showModal(josa: JosaPostResponse) {
        val dialog = CustomDialogFragment.newInstance(josa)
        dialog.show(supportFragmentManager, "JosaModal")
        Log.d("모달 확인", "모달을 띄웁니다: ${josa.josaName}") // 테스트
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            kakaoMap?.let { moveToCurrentLocation(it) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            kakaoMap?.let { moveToCurrentLocation(it) }
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
//        val formattedLatitude = String.format("%.4f", location.latitude)
//        val formattedLongitude = String.format("%.4f", location.longitude)

//        binding.latLngText.text = "위도: $formattedLatitude\n경도: $formattedLongitude"
        clickedLatitude = location.latitude
        clickedLongitude = location.longitude
    }

    private fun addLabelToLocation(kakaoMap: KakaoMap, location: LatLng) {
        locationLabel?.remove()

        val styles = LabelStyles.from(LabelStyle.from(R.drawable.my_location_icon))
        val labelOptions = LabelOptions.from(location).setStyles(styles)
        val labelLayer = kakaoMap.getLabelManager()?.getLayer()

        val label = labelLayer?.addLabel(labelOptions)
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
