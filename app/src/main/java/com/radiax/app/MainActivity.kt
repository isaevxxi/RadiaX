package com.radiax.app

import android.graphics.Bitmap
import android.net.Uri
import com.radiax.app.utils.PermissionsHelper
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.PickVisualMediaRequest
import android.graphics.ImageDecoder
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.radiax.app.repository.AnalysisRepository
import com.radiax.app.utils.NetworkResult
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var tvResult: TextView
    private var tempImageUri: Uri? = null

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            imageView.setImageURI(it)
            analyzePhoto(it)
        } ?: showToast("Failed to select image")
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageView.setImageURI(it)
            analyzePhoto(it)
        } ?: showToast("Failed to select image")
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && tempImageUri != null) {
            analyzePhoto(tempImageUri!!)
            imageView.setImageURI(tempImageUri)
        } else {
            showToast("Failed to capture image")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        btnGallery = findViewById(R.id.btnGallery)
        btnCamera = findViewById(R.id.btnCamera)
        tvResult = findViewById(R.id.tvResult)

        if (!PermissionsHelper.hasPermissions(this)) {
            PermissionsHelper.requestPermissions(this)
        }

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        btnGallery.setOnClickListener { openGallery() }
        btnCamera.setOnClickListener { openCamera() }
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    private fun openCamera() {
        if (PermissionsHelper.hasPermissions(this)) {
            tempImageUri = createTempImageUri()
            if (tempImageUri == null) {
                showToast("Error creating file for camera")
                return
            }
            cameraLauncher.launch(tempImageUri!!)
        } else {
            PermissionsHelper.requestPermissions(this)
        }
    }

    private fun createTempImageUri(): Uri? {
        val imageDir = File(filesDir, "images").apply { mkdirs() }
        imageDir.listFiles()?.forEach { it.delete() }
        val tempFile = File(imageDir, "photo_${System.currentTimeMillis()}.jpg").apply { createNewFile() }
        return FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", tempFile)
    }

    private fun analyzePhoto(photoUri: Uri) {
        lifecycleScope.launch {
            val imageBytes = processImage(photoUri)
            val result = AnalysisRepository().analyzeImage(imageBytes)

            when (result) {
                is NetworkResult.Success -> {
                    showToast("✅ Анализ завершён!")

                    // ✅ Выводим полный ответ в логи
                    println("📜 Окончательный анализ: ${result.data}")

                    findViewById<TextView>(R.id.tvResult).text = result.data
                }

                is NetworkResult.Error -> {
                    showToast(result.message)
                    println("⚠️ Ошибка: ${result.message}")
                    findViewById<TextView>(R.id.tvResult).text = "Ошибка: ${result.message}"
                }

                NetworkResult.Loading -> showToast("⏳ Анализируем...")
            }
        }
    }

    private fun processImage(uri: Uri): ByteArray {
        val originalBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        val resizedBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            originalBitmap.width / 2,
            originalBitmap.height / 2,
            true
        )

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return outputStream.toByteArray()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showToast("Permissions granted!")
            } else {
                showToast("Permissions denied. Enable them in settings.")
            }
        }
    }
}