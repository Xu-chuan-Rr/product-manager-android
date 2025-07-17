package com.productmanager.app

import android.app.Application
import com.productmanager.app.data.database.ProductDatabase
import com.productmanager.app.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ProductManagerApplication : Application() {
    
    // 应用级别的协程作用域
    val applicationScope = CoroutineScope(SupervisorJob())
    
    // 数据库实例
    val database by lazy { ProductDatabase.getDatabase(this, applicationScope) }
    
    // 仓库实例
    val productRepository by lazy { ProductRepository(database.productDao(), database.categoryDao(), database.productImageDao()) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: ProductManagerApplication
            private set
    }
}
