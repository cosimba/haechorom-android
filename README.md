# 해초롬 Android Systems

## DIVE 2024 Hackathon

이번 해커톤의 주제는 **바다환경지킴이 빅데이터 구축용 스마트 수거앱 개발**이었습니다. 이 프로젝트는 바다환경지킴이들이 해양쓰레기를 수거하는 과정에서 데이터를 수집하고, 이를 기반으로 효율적인 수거 경로를 계획하며, 수거된 쓰레기의 종류와 양을 관리할 수 있는 시스템을 만드는 것을 목표로 했습니다.
<br>

#### 해커톤에서 개발한 애플리케이션은 사용자의 로그인 정보에 따라 다른 모드를 제공하며, 각각의 모드에서 다양한 기능을 제공합니다.

- **조사 모드**: 현장에서 사진을 촬영하고, 쓰레기의 종류와 예측 수거량을 기록합니다.
- **청소 모드**: 실제로 수거된 쓰레기 양을 기록하고 사진을 업로드합니다.
- **관리자 모드**: 수거된 쓰레기 데이터를 기반으로 시각화하고, 분석 및 다운로드 기능을 제공합니다.
- **수거차량 운전자 모드**: 수거된 쓰레기 양과 이동 경로를 시각화하며, 예상 경로를 수정하고 완료 상태를 기록할 수 있습니다.
<br>

이 앱은 GPS(위도, 경도)를 활용해 쓰레기 조사 및 수거 위치를 기록하고, Retrofit 사용을 통해 데이터를 서버로 전송하여 빅데이터 구축에 기여하는 방식으로 설계되었습니다.

---

## Development Environment
- **Language** :  Kotlin
- **IDE** :  Android Studio
- **Android Studio Version** :  Koala | 2024.1.2
- **Android SDK Version** :  Android 14 (Upside Down Cake, SDK 34)
- **Required AGP Version** :  3.2~8.6, AGP 8.5.1 (July 2024)
- start with Empty Activity

---

## Features
이 안드로이드 애플리케이션은 외부 API와의 통합을 통해 지도 기반 기능 및 서버 통신을 제공합니다.

### 1. **Retrofit 통합**
   - 이 프로젝트에서 **Retrofit**을 사용하여 서버와의 연결을 설정했습니다. 이 도구는 HTTP 통신을 단순화하고 RESTful API 관리를 용이하게 해줍니다.
   - POST 방식과 GET 방식을 모두 활용하여 서버와 데이터를 주고받았습니다. POST 방식으로는 클라이언트에서 서버로 데이터를 전송하고, GET 방식으로는 서버에서 필요한 정보를 받아오는 작업을 수행했습니다.

### 2. **카카오 지도 API**
   - **카카오 지도**를 활용하여 앱 내에서 인터랙티브한 지도를 표시했습니다.
   - API 사용을 위해 키 해시 등록이 필요했으며, **네이티브 키**를 사용하여 지도를 초기화하고 표시했습니다.
   - 또한 **REST API**를 사용하여 지도 위에 핀을 표시하고, 이를 지형과 도로 경로에 연결하는 기능을 구현했습니다.

### 3. **지도 기능**
   - 이 지도는 사용자가 지정한 도로와 지형을 따라 맞춤형 마커와 폴리라인 경로를 제공하여 매끄러운 사용자 경험을 제공합니다.

---

## Dependencies
Below are the key dependencies used in this project:
```groovy
    implementation("com.kakao.sdk:v2-user:2.12.1") // 카카오 로그인 모듈 (keyHash 값 때문에 설정)
    implementation("com.kakao.maps.open:android:2.6.0") // 카카오 맵 API

    // 안드로이드 클라이언트에서 서버와 연결 => Retrofit 을 사용해 서버와 통신
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Gson
    implementation("com.google.code.gson:gson:2.8.8")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
```

## Android Manifest Permissions

이 애플리케이션은 인터넷 연결, GPS 위치 서비스, 카메라 사용, 외부 저장소 접근 등 다양한 기능을 지원하기 위해 `AndroidManifest.xml` 파일에 다음과 같은 권한 및 기능을 정의했습니다:

```xml
<uses-feature
    android:name="android.hardware.camera"
    android:required="false" /> <!-- Allow optional camera usage -->

<!-- Permissions for internet access -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Permissions for GPS location access -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Permissions for camera and photo access -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Permissions for external storage access -->
<uses-permission
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32"
    tools:ignore="ScopedStorage" />
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

---

# The results
### · Android (Frontend)
<h3>✸ 주요 기능 </h3>

<table>
	<tr>
    <td colspan="3" align="center">Main & Login</td>
	  <td colspan="3" align="center">SignUp & Mode</td>
  </tr>
  <tr>
    <td align="center"><img width="329" src="https://github.com/user-attachments/assets/bcf790a7-14db-4ed1-a196-95dcbe631fe9"/></td>
    <td align="center"><img width="329" src="https://github.com/user-attachments/assets/127672a2-8d8a-401a-bc38-9f9505b66f78"/></td>
    <td align="center"><img width="329" src="https://github.com/user-attachments/assets/e9be925e-3c9e-48b9-8778-c3a5254ab0a8"/></td>
	  <td align="center"><img width="329" src="https://github.com/user-attachments/assets/722466f1-16dd-4206-a2f7-5efa6d9fd39e"/></td>
    <td align="center"><img width="329" src="https://github.com/user-attachments/assets/2c06b8ed-a06f-42b0-875f-c2bd1d38d1f4"/></td>
    <td align="center"><img width="329" src="https://github.com/user-attachments/assets/dc4e734f-4d24-452a-b871-8dc4c8c25442"/></td>
  </tr>
  <tr>
    <td colspan="3" align="center">메인 및 로그인 화면</td>
	  <td colspan="3" align="center">회원가입 및 모드 별 지도</td>
  </tr>
</table>
<br>

## · Server (Backend)
<br>
<p align="center">
  <img width="920" alt="" src="https://github.com/user-attachments/assets/615bed25-32df-45ca-8c02-b6d1d87df906" width="80%">
</p>

---

이 프로젝트는 **DIVE 2024 해커톤** 기간 동안 개발되었습니다. 이 대회는 **2024년 10월 4일부터 10월 6일까지** 3일 동안 무박으로 진행된 이벤트입니다.

저희 3인의 참가자들은 혁신적인 솔루션을 만들기 위해 집중적으로 협력했으며, 이 프로젝트는 해커톤 기간 동안 COSIMBA팀의 노력의 결과물입니다.

이 대회는 최첨단 기술을 사용하여 실제 문제를 해결하는 것에 중점을 두었으며, 우리는 **Android Studio**, **Retrofit**, **Kakao Map API**를 활용하여 서버 통합이 가능한 지도 기반 솔루션을 개발하였습니다.

---

## Team
|<img src="https://avatars.githubusercontent.com/u/136697128?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/102722507?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/58455389?v=4" width="150" height="150"/>|
|:-:|:-:|:-:|
|eksploiter<br/>Android<br/>[@eksploiter](https://github.com/eksploiter)|dev-9hee<br/>Backend<br/>[@dev-9hee](https://github.com/dev-9hee)|sso9594<br/>Backend<br/>[@sso9594](https://github.com/sso9594)|
