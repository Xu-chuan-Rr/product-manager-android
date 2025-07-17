package com.productmanager.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    
    private const val AUTHORITY_SUFFIX = ".fileprovider"
    private const val IMAGE_DIRECTORY = "ProductImages"
    private const val VIDEO_DIRECTORY = "ProductVideos"
    private const val THUMBNAIL_DIRECTORY = "Thumbnails"
    
    // 创建图片文件
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}_"
        val storageDir = getImageDirectory(context)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
    
    // 创建视频文件
    @Throws(IOException::class)
    fun createVideoFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val videoFileName = "VID_${timeStamp}_"
        val storageDir = getVideoDirectory(context)
        return File.createTempFile(videoFileName, ".mp4", storageDir)
    }
    
    // 获取图片目录
    fun getImageDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    // 获取视频目录
    fun getVideoDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), VIDEO_DIRECTORY)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    // 获取缩略图目录
    fun getThumbnailDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), THUMBNAIL_DIRECTORY)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    // 获取文件的 Uri
    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}$AUTHORITY_SUFFIX",
            file
        )
    }
    
    // 从 Uri 获取真实路径
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var result: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (columnIndex >= 0) {
                    result = it.getString(columnIndex)
                }
            }
        }
        return result
    }
    
    // 复制文件到应用目录
    fun copyFileToAppDirectory(context: Context, sourceUri: Uri, targetFile: File): Boolean {
        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    // 获取文件大小
    fun getFileSize(file: File): Long {
        return if (file.exists()) file.length() else 0
    }
    
    // 获取文件 MIME 类型
    fun getMimeType(file: File): String? {
        val extension = file.extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    
    // 检查是否为图片文件
    fun isImageFile(file: File): Boolean {
        val mimeType = getMimeType(file)
        return mimeType?.startsWith("image/") == true
    }
    
    // 检查是否为视频文件
    fun isVideoFile(file: File): Boolean {
        val mimeType = getMimeType(file)
        return mimeType?.startsWith("video/") == true
    }
    
    // 压缩图片
    fun compressImage(
        context: Context,
        sourceFile: File,
        targetFile: File,
        quality: Int = 80,
        maxWidth: Int = 1920,
        maxHeight: Int = 1080
    ): Boolean {
        return try {
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
            val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)
            
            FileOutputStream(targetFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            
            bitmap.recycle()
            scaledBitmap.recycle()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    // 缩放图片
    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }
        
        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scale = minOf(scaleWidth, scaleHeight)
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    // 创建缩略图
    fun createThumbnail(
        context: Context,
        sourceFile: File,
        thumbnailSize: Int = 200
    ): File? {
        return try {
            val thumbnailFile = File(
                getThumbnailDirectory(context),
                "thumb_${sourceFile.nameWithoutExtension}.jpg"
            )
            
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
            val thumbnail = Bitmap.createScaledBitmap(bitmap, thumbnailSize, thumbnailSize, true)
            
            FileOutputStream(thumbnailFile).use { out ->
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            
            bitmap.recycle()
            thumbnail.recycle()
            thumbnailFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // 删除文件
    fun deleteFile(file: File): Boolean {
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
    
    // 格式化文件大小
    fun formatFileSize(bytes: Long): String {
        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024
        
        return when {
            bytes >= gb -> String.format("%.1f GB", bytes / gb)
            bytes >= mb -> String.format("%.1f MB", bytes / mb)
            bytes >= kb -> String.format("%.1f KB", bytes / kb)
            else -> "$bytes B"
        }
    }
}
