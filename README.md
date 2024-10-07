# 해초롬 Android Systems

## Development Environment
- **Language** :  Kotlin
- **IDE** :  Android Studio
- **Android Studio Version** :  Koala | 2024.1.2
- **Android SDK Version** :  Android 14 (Upside Down Cake, SDK 34)
- **Required AGP Version** :  3.2~8.6, AGP 8.5.1 (July 2024)
- start with Empty Activity

---

## Features
This Android application integrates with external APIs to provide map-based functionality and server communication.

### 1. **Retrofit Integration**
   - **Retrofit** was used to establish server connections for this project. It simplifies HTTP communication and makes it easier to manage RESTful APIs.

### 2. **Kakao Map API**
   - **Kakao Map** was implemented to display interactive maps within the app.
   - Key hash registration was required for the API, and the **native key** was used to initialize and display the map.
   - The application also used **REST API** to place pins on the map, linking them along terrain and road paths.

### 3. **Map Features**
   - The map includes custom markers and polyline paths that follow specific roads and terrain, providing a seamless user experience.
<br>

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

The following permissions and features are defined in the `AndroidManifest.xml` to support various functionalities such as internet access, GPS location, camera usage, and external storage:

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
