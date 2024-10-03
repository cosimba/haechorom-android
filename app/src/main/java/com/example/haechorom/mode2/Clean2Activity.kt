package com.example.haechorom.mode2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haechorom.R

class Clean2Activity : AppCompatActivity() {

    private lateinit var coastName: EditText
    private lateinit var coastLength: EditText
    private lateinit var pickup: EditText
    private lateinit var avatars: ImageView
    private lateinit var trashSpinner: Spinner
    private lateinit var completionBtn: Button
    private lateinit var cameraBtn: Button
    private lateinit var galleryBtn: Button
    private lateinit var latitudeTextView: TextView // 위도 표시 TextView
    private lateinit var longitudeTextView: TextView // 경도 표시 TextView
    private val CAMERA_CODE = 98
    private val GALLERY_CODE = 99

    // 추가: Mode1Activity에서 받은 위도, 경도 값 저장용 변수
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clean2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        coastName = findViewById(R.id.coastName)
        coastLength = findViewById(R.id.coastLength)
        pickup = findViewById(R.id.pickup)
        avatars = findViewById(R.id.avatars)
        trashSpinner = findViewById(R.id.trashSpinner)
        completionBtn = findViewById(R.id.joinBtn) // 완료 버튼
        cameraBtn = findViewById(R.id.camera)
        galleryBtn = findViewById(R.id.picture)
        latitudeTextView = findViewById(R.id.latitude) // 위도 TextView
        longitudeTextView = findViewById(R.id.longitude) // 경도 TextView

        // Mode1Activity에서 위도와 경도를 받아옴
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        // 위도와 경도 값을 TextView에 표시
        latitudeTextView.text = latitude.toString()
        longitudeTextView.text = longitude.toString()

        // 카메라와 갤러리 이벤트 처리
        cameraBtn.setOnClickListener { openCamera() }
        galleryBtn.setOnClickListener { openGallery() }

        // 청소 완료 버튼 이벤트 처리
        completionBtn.setOnClickListener {
            submitData()
        }
    }

    private fun openCamera() {
        if (checkPermission(arrayOf(Manifest.permission.CAMERA), CAMERA_CODE)) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_CODE)
        }
    }

    private fun openGallery() {
        if (checkPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_CODE)) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    avatars.setImageBitmap(imageBitmap)
                }
                GALLERY_CODE -> {
                    val imageUri: Uri? = data?.data
                    avatars.setImageURI(imageUri)
                }
            }
        }
    }

    private fun submitData() {
        val name = coastName.text.toString()
        val length = coastLength.text.toString()
        val pickupAmount = pickup.text.toString()
        val trashType = trashSpinner.selectedItem.toString()

        if (name.isEmpty() || length.isEmpty() || pickupAmount.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_LONG).show()
            return
        }

        // 청소 데이터를 Mode2Activity로 전달하고 완료 처리
        val intent = Intent(this, Mode2Activity::class.java)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        intent.putExtra("coastName", name)
        intent.putExtra("pickupAmount", pickupAmount)
        startActivity(intent)

        Toast.makeText(this, "청소 완료", Toast.LENGTH_SHORT).show()

        finish() // Clean2Activity를 종료하고 Mode2Activity로 돌아감
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, requestCode)
                return false
            }
        }
        return true
    }
}
