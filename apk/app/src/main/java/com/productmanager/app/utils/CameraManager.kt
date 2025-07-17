package com.productmanager.app.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.File

class CameraManager(
    private val fragment: Fragment,
    private val onImageCaptured: (File) -> Unit,
    private val onVideoCaptured: (File) -> Unit,
    private val onImageSelected: (Uri) -> Unit,
    private val onVideoSelected: (Uri) -> Unit,
    private val onMultipleImagesSelected: (List<Uri>) -> Unit,
    private val onError: (String) -> Unit
) {
    
    private var currentPhotoFile: File? = null
    private var currentVideoFile: File? = null
    
    // 拍照启动器
    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoFile?.let { file ->
                    if (file.exists()) {
                        onImageCaptured(file)
                    } else {
                        onError("拍照失败：文件不存在")
                    }
                }
            } else {
                onError("拍照被取消")
            }
        }
    
    // 录像启动器
    private val captureVideoLauncher: ActivityResultLauncher<Uri> =
        fragment.registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) {
                currentVideoFile?.let { file ->
                    if (file.exists()) {
                        onVideoCaptured(file)
                    } else {
                        onError("录像失败：文件不存在")
                    }
                }
            } else {
                onError("录像被取消")
            }
        }
    
    // 选择单张图片启动器
    private val pickImageLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onImageSelected(it) } ?: onError("未选择图片")
        }
    
    // 选择单个视频启动器
    private val pickVideoLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onVideoSelected(it) } ?: onError("未选择视频")
        }
    
    // 选择多张图片启动器
    private val pickMultipleImagesLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                onMultipleImagesSelected(uris)
            } else {
                onError("未选择图片")
            }
        }
    
    // 拍照
    fun takePicture() {
        try {
            val context = fragment.requireContext()
            if (!PermissionUtils.hasCameraPermission(context)) {
                onError("没有相机权限")
                return
            }
            
            currentPhotoFile = FileUtils.createImageFile(context)
            val photoUri = FileUtils.getFileUri(context, currentPhotoFile!!)
            takePictureLauncher.launch(photoUri)
        } catch (e: Exception) {
            onError("启动相机失败：${e.message}")
        }
    }
    
    // 录像
    fun captureVideo() {
        try {
            val context = fragment.requireContext()
            if (!PermissionUtils.hasCameraPermission(context)) {
                onError("没有相机权限")
                return
            }
            
            currentVideoFile = FileUtils.createVideoFile(context)
            val videoUri = FileUtils.getFileUri(context, currentVideoFile!!)
            captureVideoLauncher.launch(videoUri)
        } catch (e: Exception) {
            onError("启动录像失败：${e.message}")
        }
    }
    
    // 从相册选择单张图片
    fun pickImageFromGallery() {
        val context = fragment.requireContext()
        if (!PermissionUtils.hasStoragePermission(context)) {
            onError("没有存储权限")
            return
        }
        pickImageLauncher.launch("image/*")
    }
    
    // 从相册选择视频
    fun pickVideoFromGallery() {
        val context = fragment.requireContext()
        if (!PermissionUtils.hasStoragePermission(context)) {
            onError("没有存储权限")
            return
        }
        pickVideoLauncher.launch("video/*")
    }
    
    // 从相册选择多张图片
    fun pickMultipleImagesFromGallery() {
        val context = fragment.requireContext()
        if (!PermissionUtils.hasStoragePermission(context)) {
            onError("没有存储权限")
            return
        }
        pickMultipleImagesLauncher.launch("image/*")
    }
    
    // 显示选择对话框
    fun showImagePickerDialog() {
        val context = fragment.requireContext()
        val options = arrayOf("拍照", "从相册选择", "批量选择")
        
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("选择图片")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> pickImageFromGallery()
                    2 -> pickMultipleImagesFromGallery()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    // 显示视频选择对话框
    fun showVideoPickerDialog() {
        val context = fragment.requireContext()
        val options = arrayOf("录像", "从相册选择")
        
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("选择视频")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureVideo()
                    1 -> pickVideoFromGallery()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
