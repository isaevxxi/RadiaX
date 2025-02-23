package com.radiax.app.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsHelper {
    private const val REQUEST_CODE_PERMISSIONS = 101

    // Список нужных разрешений в зависимости от версии Android
    private val REQUIRED_PERMISSIONS: Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
            Manifest.permission.CAMERA
        )
        else -> arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    // Проверяем, есть ли все нужные разрешения
    fun hasPermissions(activity: Activity): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Запрашиваем разрешения у пользователя
    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }
}