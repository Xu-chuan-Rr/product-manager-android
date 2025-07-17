package com.productmanager.app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtils {
    
    const val REQUEST_CODE_PERMISSIONS = 1001
    
    // 获取所需的权限列表
    fun getRequiredPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用新的媒体权限
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            // Android 12 及以下使用传统权限
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
    
    // 检查是否有相机权限
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    // 检查是否有存储权限
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    // 检查是否有所有必需权限
    fun hasAllPermissions(context: Context): Boolean {
        return getRequiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    // 使用 Dexter 请求权限
    fun requestPermissions(
        activity: Activity,
        onPermissionsGranted: () -> Unit,
        onPermissionsDenied: (List<String>) -> Unit,
        onPermissionsPermanentlyDenied: (List<String>) -> Unit
    ) {
        Dexter.withActivity(activity)
            .withPermissions(getRequiredPermissions())
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    when {
                        report.areAllPermissionsGranted() -> {
                            onPermissionsGranted()
                        }
                        report.isAnyPermissionPermanentlyDenied -> {
                            val permanentlyDeniedPermissions = report.deniedPermissionResponses
                                .filter { it.isPermanentlyDenied }
                                .map { it.permissionName }
                            onPermissionsPermanentlyDenied(permanentlyDeniedPermissions)
                        }
                        else -> {
                            val deniedPermissions = report.deniedPermissionResponses
                                .map { it.permissionName }
                            onPermissionsDenied(deniedPermissions)
                        }
                    }
                }
                
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }
    
    // 打开应用设置页面
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
    
    // 检查权限是否被永久拒绝
    fun isPermissionPermanentlyDenied(activity: Activity, permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) &&
                ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
    }
    
    // 获取权限的友好名称
    fun getPermissionName(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> "相机"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "存储读取"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "存储写入"
            Manifest.permission.READ_MEDIA_IMAGES -> "图片访问"
            Manifest.permission.READ_MEDIA_VIDEO -> "视频访问"
            else -> "未知权限"
        }
    }
}
