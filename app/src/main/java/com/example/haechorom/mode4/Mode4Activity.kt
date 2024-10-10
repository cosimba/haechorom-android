package com.example.haechorom.mode4

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.haechorom.R
import com.example.haechorom.api.dto.response.CollectPostResponse
import com.example.haechorom.databinding.ActivityMode4Binding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Mode4Activity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var binding: ActivityMode4Binding
    private var kakaoMap: KakaoMap? = null
    private val pinLabels = mutableMapOf<LatLng, Label>()

    private val greenPinLocations = mutableListOf<LatLng>()

    private var totalCollectBag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMode4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermission()

        binding.mapView.start(object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                setupMap()
            }
        })

        binding.button.setOnClickListener { connectGreenPinsWithRoad() }
    }

    // 벡스코 근처로 임의의 좌표 값을 설정
    private fun setupMap() {
        moveToCurrentLocation()
        addPinToLocation(35.168000, 129.130000)
        addPinToLocation(35.165000, 129.133000)
        addPinToLocation(35.160000, 129.131000)
        addPinToLocation(35.161000, 129.130000)
        setLabelClickListener()
    }

    private fun connectGreenPinsWithRoad() {
        if (greenPinLocations.isNotEmpty()) {
            val start = getCurrentLocationLatLng() // Get current location dynamically
            val end = greenPinLocations.last()

            fetchRoadDirections(start.latitude, start.longitude, end.latitude, end.longitude)
        } else {
            Toast.makeText(this, "적어도 하나 이상의 초록 핀이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocationLatLng(): LatLng {
        // Check if location permissions are granted, then retrieve the current location
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location? = if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else null

        return location?.let {
            LatLng.from(it.latitude, it.longitude)
        } ?: LatLng.from(35.1678916, 129.134145) // Default fallback location
    }

    // * 현재 보이는 API 와 같은 주소, 권한과 관련된 부분은 현재 이용할 수 없게 돼있으며
    // 전부 학습 목적으로 업로드 되어 있습니다. *

    private fun fetchRoadDirections(startLat: Double, startLng: Double, endLat: Double, endLng: Double) {
        val url = "https://apis-navi.kakaomobility.com/v1/directions?origin=$startLng,$startLat&destination=$endLng,$endLat"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "KakaoAK '여기에 REST API'")
            // 지도에 건물, 도로, 지형 등을 고려한 경로 설정을 위함
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseData ->
                        val coordinates = parseRouteCoordinates(responseData)
                        runOnUiThread {
                            if (coordinates.isNotEmpty()) drawRouteLine(coordinates)
                        }
                    }
                }
            }
        })
    }

    private fun parseRouteCoordinates(responseData: String): List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        val sections = JSONObject(responseData).getJSONArray("routes")
            .getJSONObject(0)
            .getJSONArray("sections")

        for (i in 0 until sections.length()) {
            val roads = sections.getJSONObject(i).getJSONArray("roads")
            for (j in 0 until roads.length()) {
                val vertexes = roads.getJSONObject(j).getJSONArray("vertexes")
                for (k in 0 until vertexes.length() step 2) {
                    coordinates.add(LatLng.from(vertexes.getDouble(k + 1), vertexes.getDouble(k)))
                }
            }
        }
        return coordinates
    }

    private fun drawRouteLine(coordinates: List<LatLng>) {
        kakaoMap?.let { map ->
            val segments = coordinates.windowed(2).map {
                RouteLineSegment.from(it).setStyles(routeStyle())
            }
            map.getRouteLineManager()?.getLayer()?.addRouteLine(RouteLineOptions.from(segments))
        }
    }

    private fun routeStyle() = RouteLineStylesSet.from(
        "blueStyle",
        RouteLineStyles.from(RouteLineStyle.from(20f, 0xFF3E8EDE.toInt()))
    ).getStyles(0)

    private fun moveToCurrentLocation() {
        val myLocation = getCurrentLocationLatLng()
        kakaoMap?.let { map ->
            val labelOptions = LabelOptions.from(myLocation).setStyles(LabelStyles.from(LabelStyle.from(R.drawable.my_location_icon)))
            map.getLabelManager()?.getLayer()?.addLabel(labelOptions)

            val cameraPosition = CameraPosition.from(myLocation.latitude, myLocation.longitude, 10, 0.0, 0.0, 0.0)
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

//    // 청소 기록 가져오기
//    private fun loadCleanRecords() {
//        RetrofitClient.apiService.getCleanFinished().enqueue(object : Callback<List<CollectPostResponse>> {
//            override fun onResponse(call: Call<List<CollectPostResponse>>, response: Response<List<CollectPostResponse>>) {
//                if (response.isSuccessful) {
//                    val cleanList = response.body()
//                    Log.d("청소 내역", cleanList.toString())
//                    cleanList?.let {
//                        addCleanMarkersToMap(it)
//                    }
//                } else {
//                    Log.d("청소 내역", "응답 실패: ${response.code()}")
//                    Toast.makeText(this@Mode4Activity, "청소 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<CollectPostResponse>>, t: Throwable) {
//                Log.e("청소 내역", "에러: ${t.message}")
//                Toast.makeText(this@Mode4Activity, "청소 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun addCleanMarkersToMap(cleanList: List<CollectPostResponse>) {
        cleanList.forEach { clean ->
            val position = LatLng.from(clean.lat, clean.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.blue_pin))

            // 라벨에 collectBag 값을 텍스트로 표시
            val labelOptions = LabelOptions.from(position)
                .setStyles(styles)
                .setTexts(clean.collectBag.toString()) // collectBag 값을 텍스트로 추가

            val labelLayer = kakaoMap?.getLabelManager()?.getLayer()

            labelLayer?.let {
                val label = it.addLabel(labelOptions)
                label?.let { addedLabel ->
                    pinLabels[position] = addedLabel
                    Log.d("라벨 추가", "라벨이 추가되었습니다: ${clean.collectBag}")
                }
            }
        }
    }


    private fun addPinToLocation(latitude: Double, longitude: Double) {
        kakaoMap?.let { map ->
            val location = LatLng.from(latitude, longitude)
            val labelOptions = LabelOptions.from(location).setStyles(LabelStyles.from(LabelStyle.from(R.drawable.blue_pin)))
            map.getLabelManager()?.getLayer()?.addLabel(labelOptions)?.let {
                pinLabels[location] = it
            }
        }
    }

    private fun setLabelClickListener() {
        kakaoMap?.setOnLabelClickListener { map, _, label ->
            pinLabels.entries.firstOrNull { it.value == label }?.key?.let { labelLocation ->
                updateLabelToGreenPin(map, labelLocation)
            }
        }
    }

    private fun updateLabelToGreenPin(map: KakaoMap, labelLocation: LatLng) {
        pinLabels[labelLocation]?.remove()
        val greenPinStyle = LabelStyles.from(LabelStyle.from(R.drawable.green_pin))
        val labelOptions = LabelOptions.from(labelLocation).setStyles(greenPinStyle)
        map.getLabelManager()?.getLayer()?.addLabel(labelOptions)?.let {
            pinLabels[labelLocation] = it
            greenPinLocations.add(labelLocation)

            // Update total collectBag and truck type
            val collectBagValue = getCollectBagValue(labelLocation)
            totalCollectBag += collectBagValue
            updateUIForTotalCollectBag(totalCollectBag)
        }
    }

    private fun getCollectBagValue(labelLocation: LatLng): Int {
        // Example logic to return collectBag value based on the label location
        return 10 // Assuming each location contributes 10 bags as an example
    }

    private fun updateUIForTotalCollectBag(totalCollectBag: Int) {
        // Update the total number of bags and calculate truck type based on weight
        val totalWeight = totalCollectBag * 50 // Each bag weighs 50kg
        val truckType = getTruckType(totalWeight)

        findViewById<TextView>(R.id.total_num).text = "· 총 마대 갯수 : $totalCollectBag"
        findViewById<TextView>(R.id.truck).text = "· 필요 차량 : $truckType 트럭"
    }

    // 마대 자루의 총 무게에 따른 필요 톤수의 차량 - 좀 더 세밀하게 설정 필요
    private fun getTruckType(totalWeight: Int): String {
        return when {
            totalWeight <= 1000 -> "1톤"
            totalWeight <= 2000 -> "2톤"
            totalWeight <= 3000 -> "3톤"
            totalWeight <= 4000 -> "4톤"
            else -> "5톤"
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            moveToCurrentLocation()
        }
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
