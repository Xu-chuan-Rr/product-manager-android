package com.productmanager.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "product_videos",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productId"])]
)
data class ProductVideo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val productId: Long, // 关联的商品ID
    val videoPath: String, // 视频本地路径
    val videoUrl: String? = null, // 视频网络URL
    val thumbnailPath: String? = null, // 视频缩略图路径
    val duration: Long? = null, // 视频时长（毫秒）
    val width: Int? = null, // 视频宽度
    val height: Int? = null, // 视频高度
    val fileSize: Long? = null, // 文件大小（字节）
    val mimeType: String? = null, // MIME类型
    val bitrate: Int? = null, // 比特率
    val frameRate: Float? = null, // 帧率
    val description: String? = null, // 视频描述
    val sortOrder: Int = 0, // 排序顺序
    val isMain: Boolean = false, // 是否为主视频
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
