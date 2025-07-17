package com.productmanager.app.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.productmanager.app.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageEditUtils {
    
    // 旋转图片
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    // 翻转图片
    fun flipBitmap(bitmap: Bitmap, horizontal: Boolean = true): Bitmap {
        val matrix = Matrix()
        if (horizontal) {
            matrix.preScale(-1.0f, 1.0f)
        } else {
            matrix.preScale(1.0f, -1.0f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    // 调整亮度
    fun adjustBrightness(bitmap: Bitmap, brightness: Float): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(floatArrayOf(
            1f, 0f, 0f, 0f, brightness,
            0f, 1f, 0f, 0f, brightness,
            0f, 0f, 1f, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 调整对比度
    fun adjustContrast(bitmap: Bitmap, contrast: Float): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(floatArrayOf(
            contrast, 0f, 0f, 0f, 0f,
            0f, contrast, 0f, 0f, 0f,
            0f, 0f, contrast, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 调整饱和度
    fun adjustSaturation(bitmap: Bitmap, saturation: Float): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(saturation)
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 应用颜色矩阵
    private fun applyColorMatrix(bitmap: Bitmap, colorMatrix: ColorMatrix): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }
    
    // 应用灰度滤镜
    fun applyGrayscaleFilter(bitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 应用复古滤镜
    fun applyVintageFilter(bitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 应用暖色调滤镜
    fun applyWarmFilter(bitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(floatArrayOf(
            1.2f, 0f, 0f, 0f, 0f,
            0f, 1.0f, 0f, 0f, 0f,
            0f, 0f, 0.8f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 应用冷色调滤镜
    fun applyCoolFilter(bitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(floatArrayOf(
            0.8f, 0f, 0f, 0f, 0f,
            0f, 1.0f, 0f, 0f, 0f,
            0f, 0f, 1.2f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        return applyColorMatrix(bitmap, colorMatrix)
    }
    
    // 添加文字水印
    fun addTextWatermark(
        bitmap: Bitmap,
        text: String,
        textSize: Float = 48f,
        color: Int = Color.WHITE,
        alpha: Int = 128,
        position: WatermarkPosition = WatermarkPosition.BOTTOM_RIGHT
    ): Bitmap {
        val result = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(result)
        
        val paint = Paint().apply {
            this.color = color
            this.alpha = alpha
            this.textSize = textSize
            this.isAntiAlias = true
            this.typeface = Typeface.DEFAULT_BOLD
            this.setShadowLayer(2f, 2f, 2f, Color.BLACK)
        }
        
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        
        val x = when (position) {
            WatermarkPosition.TOP_LEFT, WatermarkPosition.BOTTOM_LEFT -> 20f
            WatermarkPosition.TOP_RIGHT, WatermarkPosition.BOTTOM_RIGHT -> 
                bitmap.width - textBounds.width() - 20f
            WatermarkPosition.CENTER -> (bitmap.width - textBounds.width()) / 2f
        }
        
        val y = when (position) {
            WatermarkPosition.TOP_LEFT, WatermarkPosition.TOP_RIGHT -> 
                textBounds.height() + 20f
            WatermarkPosition.BOTTOM_LEFT, WatermarkPosition.BOTTOM_RIGHT -> 
                bitmap.height - 20f
            WatermarkPosition.CENTER -> (bitmap.height + textBounds.height()) / 2f
        }
        
        canvas.drawText(text, x, y, paint)
        return result
    }
    
    // 添加图片水印
    fun addImageWatermark(
        bitmap: Bitmap,
        watermarkBitmap: Bitmap,
        alpha: Int = 128,
        position: WatermarkPosition = WatermarkPosition.BOTTOM_RIGHT,
        scale: Float = 0.2f
    ): Bitmap {
        val result = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(result)
        
        val scaledWatermark = Bitmap.createScaledBitmap(
            watermarkBitmap,
            (watermarkBitmap.width * scale).toInt(),
            (watermarkBitmap.height * scale).toInt(),
            true
        )
        
        val paint = Paint().apply {
            this.alpha = alpha
        }
        
        val x = when (position) {
            WatermarkPosition.TOP_LEFT, WatermarkPosition.BOTTOM_LEFT -> 20f
            WatermarkPosition.TOP_RIGHT, WatermarkPosition.BOTTOM_RIGHT -> 
                bitmap.width - scaledWatermark.width - 20f
            WatermarkPosition.CENTER -> (bitmap.width - scaledWatermark.width) / 2f
        }
        
        val y = when (position) {
            WatermarkPosition.TOP_LEFT, WatermarkPosition.TOP_RIGHT -> 20f
            WatermarkPosition.BOTTOM_LEFT, WatermarkPosition.BOTTOM_RIGHT -> 
                bitmap.height - scaledWatermark.height - 20f
            WatermarkPosition.CENTER -> (bitmap.height - scaledWatermark.height) / 2f
        }
        
        canvas.drawBitmap(scaledWatermark, x, y, paint)
        scaledWatermark.recycle()
        return result
    }
    
    // 保存Bitmap到文件
    fun saveBitmapToFile(
        context: Context,
        bitmap: Bitmap,
        filename: String,
        quality: Int = 90
    ): File? {
        return try {
            val file = File(FileUtils.getImageDirectory(context), filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    // 创建圆角图片
    fun createRoundedBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
        
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        return output
    }
    
    // 创建圆形图片
    fun createCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
        
        val rect = Rect(0, 0, size, size)
        
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        return output
    }
    
    enum class WatermarkPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER
    }
}
