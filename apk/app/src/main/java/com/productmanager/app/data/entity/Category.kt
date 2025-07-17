package com.productmanager.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val description: String? = null,
    val color: String? = null, // 分类颜色标识
    val iconPath: String? = null, // 分类图标路径
    val sortOrder: Int = 0, // 排序顺序
    val isActive: Boolean = true, // 是否启用
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
