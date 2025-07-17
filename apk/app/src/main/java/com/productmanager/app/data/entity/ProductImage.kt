package com.productmanager.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "product_images",
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
data class ProductImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val productId: Long, // 关联的商品ID
    val imagePath: String, // 图片本地路径
    val imageUrl: String? = null, // 图片网络URL
    val thumbnailPath: String? = null, // 缩略图路径
    val imageType: ImageType = ImageType.PRODUCT, // 图片类型
    val sortOrder: Int = 0, // 排序顺序
    val isMain: Boolean = false, // 是否为主图
    val width: Int? = null, // 图片宽度
    val height: Int? = null, // 图片高度
    val fileSize: Long? = null, // 文件大小（字节）
    val mimeType: String? = null, // MIME类型
    val description: String? = null, // 图片描述
    val altText: String? = null, // 替代文本
    val watermarkApplied: Boolean = false, // 是否已添加水印
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class ImageType {
    PRODUCT,    // 商品图片
    DETAIL,     // 详情图片
    THUMBNAIL,  // 缩略图
    WATERMARK,  // 水印图片
    BANNER      // 横幅图片
}
