package com.example.haechorom.mode4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.kakao.vectormap.label.*
import com.kakao.vectormap.route.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Mode4Activity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var binding: ActivityMode4Binding
    private var kakaoMap: KakaoMap? = null
    private val pinLabels = mutableMapOf<LatLng, Label>()
    private val greenPinLocations = mutableListOf<LatLng>()

    private val fixedLatitude = 35.1678916
    private val fixedLongitude = 129.134145

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

    private fun setupMap() {
        moveToFixedLocation()
        addPinToLocation(35.168000, 129.130000)
        addPinToLocation(35.165000, 129.133000)
        addPinToLocation(35.160000, 129.131000)
        addPinToLocation(35.161000, 129.130000)
        setLabelClickListener()
    }

    private fun connectGreenPinsWithRoad() {
        if (greenPinLocations.isNotEmpty()) {
            // 고정된 위치 (my_location_icon)부터 시작
            val start = LatLng.from(fixedLatitude, fixedLongitude)
            val end = greenPinLocations.last()

            fetchRoadDirections(start.latitude, start.longitude, end.latitude, end.longitude)
        } else {
            Toast.makeText(this, "적어도 하나 이상의 초록 핀이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRoadDirections(startLat: Double, startLng: Double, endLat: Double, endLng: Double) {
        val url = "https://apis-navi.kakaomobility.com/v1/directions?origin=$startLng,$startLat&destination=$endLng,$endLat"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "KakaoAK 96ddc95fc9943db0616e923c34a3fe65")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseData ->
                        Log.d("API Response", "Response Data: $responseData")
                        val coordinates = parseRouteCoordinates(responseData)
                        runOnUiThread {
                            if (coordinates.isNotEmpty()) drawRouteLine(coordinates)
                            else Log.e("Parse Error", "No valid coordinates received.")
                        }
                    } ?: Log.e("API Error", "No response data received.")
                } else Log.e("API Error", "Response not successful: ${response.message}")
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
        Log.d("Coordinates", coordinates.toString())
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

    private fun routeStyle() = RouteLineStylesSet.from("blueStyle", RouteLineStyles.from(RouteLineStyle.from(20f, 0xFF3E8EDE.toInt()))).getStyles(0)

    private fun moveToFixedLocation() {
        kakaoMap?.let { map ->
            val myLocation = LatLng.from(fixedLatitude, fixedLongitude)
            val labelOptions = LabelOptions.from(myLocation).setStyles(LabelStyles.from(LabelStyle.from(R.drawable.my_location_icon)))
            map.getLabelManager()?.getLayer()?.addLabel(labelOptions)

            val cameraPosition = CameraPosition.from(myLocation.latitude, myLocation.longitude, 10, 0.0, 0.0, 0.0)
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            moveToFixedLocation()
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
