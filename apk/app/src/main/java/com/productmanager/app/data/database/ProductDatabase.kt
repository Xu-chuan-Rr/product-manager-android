package com.productmanager.app.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.productmanager.app.data.converter.Converters
import com.productmanager.app.data.dao.CategoryDao
import com.productmanager.app.data.dao.ProductDao
import com.productmanager.app.data.dao.ProductImageDao
import com.productmanager.app.data.dao.ProductVideoDao
import com.productmanager.app.data.entity.Category
import com.productmanager.app.data.entity.Product
import com.productmanager.app.data.entity.ProductImage
import com.productmanager.app.data.entity.ProductVideo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Database(
    entities = [
        Category::class,
        Product::class,
        ProductImage::class,
        ProductVideo::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ProductDatabase : RoomDatabase() {
    
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun productImageDao(): ProductImageDao
    abstract fun productVideoDao(): ProductVideoDao
    
    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null
        
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ProductDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                )
                .addCallback(ProductDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    private class ProductDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.categoryDao())
                }
            }
        }
        
        suspend fun populateDatabase(categoryDao: CategoryDao) {
            // 添加默认分类
            val defaultCategories = listOf(
                Category(
                    name = "服装",
                    description = "各类服装商品",
                    color = "#FF5722",
                    sortOrder = 1,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                Category(
                    name = "数码",
                    description = "数码电子产品",
                    color = "#2196F3",
                    sortOrder = 2,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                Category(
                    name = "家居",
                    description = "家居用品",
                    color = "#4CAF50",
                    sortOrder = 3,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                Category(
                    name = "美妆",
                    description = "美妆护肤产品",
                    color = "#E91E63",
                    sortOrder = 4,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                Category(
                    name = "食品",
                    description = "食品饮料",
                    color = "#FF9800",
                    sortOrder = 5,
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )
            
            categoryDao.insertCategories(defaultCategories)
        }
    }
}
