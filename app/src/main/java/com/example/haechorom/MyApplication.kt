package com.example.haechorom

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 카카오맵 SDK 초기화
        // 네이티브 앱 키
        KakaoMapSdk.init(this, "77207433fc5c0400146ad099e660e89e")
    }
}
