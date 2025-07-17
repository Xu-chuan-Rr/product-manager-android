package com.productmanager.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String, // 商品标题
    val description: String? = null, // 商品描述
    val price: BigDecimal, // 售价
    val originalPrice: BigDecimal? = null, // 原价（划线价）
    val stock: Int = 0, // 库存数量
    val categoryId: Long? = null, // 分类ID
    val tags: String? = null, // 标签，用逗号分隔
    val attributes: String? = null, // 商品属性JSON字符串
    val sku: String? = null, // 商品SKU
    val barcode: String? = null, // 条形码
    val weight: Double? = null, // 重量（克）
    val dimensions: String? = null, // 尺寸信息
    val material: String? = null, // 材质
    val brand: String? = null, // 品牌
    val status: ProductStatus = ProductStatus.ACTIVE, // 商品状态
    val sortOrder: Int = 0, // 排序顺序
    val viewCount: Int = 0, // 查看次数
    val salesCount: Int = 0, // 销售数量
    val isRecommended: Boolean = false, // 是否推荐
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class ProductStatus {
    ACTIVE,    // 正常销售
    INACTIVE,  // 下架
    DRAFT,     // 草稿
    OUT_OF_STOCK // 缺货
}
