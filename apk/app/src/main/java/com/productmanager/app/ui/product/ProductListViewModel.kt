package com.productmanager.app.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.productmanager.app.data.entity.Product
import com.productmanager.app.data.entity.ProductStatus
import com.productmanager.app.data.repository.ProductRepository
import kotlinx.coroutines.launch
import java.util.Date

class ProductListViewModel(private val repository: ProductRepository) : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 搜索查询
    private val _searchQuery = MutableLiveData<String>()
    
    // 状态筛选
    private val _statusFilter = MutableLiveData<ProductStatus?>()
    
    // 排序方式
    private val _sortBy = MutableLiveData<String>()
    
    // 商品列表
    val products: LiveData<List<Product>> = _searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            _statusFilter.switchMap { status ->
                _sortBy.switchMap { sortBy ->
                    repository.getFilteredProducts(
                        status = status,
                        sortBy = sortBy ?: "updated"
                    ).asLiveData()
                }
            }
        } else {
            repository.searchProducts(query).asLiveData()
        }
    }
    
    init {
        // 初始化默认值
        _searchQuery.value = ""
        _statusFilter.value = null
        _sortBy.value = "updated"
    }
    
    // 搜索商品
    fun searchProducts(query: String) {
        _searchQuery.value = query
    }
    
    // 清除搜索
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    // 按状态筛选
    fun filterByStatus(status: ProductStatus?) {
        _statusFilter.value = status
    }
    
    // 排序
    fun sortProducts(sortBy: String) {
        _sortBy.value = sortBy
    }
    
    // 删除商品
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 删除关联的图片
                repository.deleteImagesByProductId(productId)
                
                // 删除商品
                repository.deleteProductById(productId)
                
            } catch (e: Exception) {
                _errorMessage.value = "删除商品失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 复制商品
    fun duplicateProduct(productId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val originalProduct = repository.getProductById(productId)
                originalProduct?.let { product ->
                    val duplicatedProduct = product.copy(
                        id = 0, // 新商品ID由数据库自动生成
                        title = "${product.title} (副本)",
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                    
                    val newProductId = repository.insertProduct(duplicatedProduct)
                    
                    // 复制商品图片
                    val images = repository.getImagesByProductId(productId).asLiveData().value
                    images?.forEach { image ->
                        val duplicatedImage = image.copy(
                            id = 0,
                            productId = newProductId,
                            isMain = false, // 复制的图片不设为主图
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        repository.insertImage(duplicatedImage)
                    }
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "复制商品失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 批量删除商品
    fun deleteProducts(productIds: List<Long>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                productIds.forEach { productId ->
                    // 删除关联的图片
                    repository.deleteImagesByProductId(productId)
                    // 删除商品
                    repository.deleteProductById(productId)
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "批量删除失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 更新商品状态
    fun updateProductStatus(productId: Long, status: ProductStatus) {
        viewModelScope.launch {
            try {
                val product = repository.getProductById(productId)
                product?.let {
                    val updatedProduct = it.copy(
                        status = status,
                        updatedAt = Date()
                    )
                    repository.updateProduct(updatedProduct)
                }
            } catch (e: Exception) {
                _errorMessage.value = "更新商品状态失败：${e.message}"
            }
        }
    }
    
    // 获取商品统计信息
    fun getProductStats(): LiveData<ProductStats> {
        val statsLiveData = MutableLiveData<ProductStats>()
        
        viewModelScope.launch {
            try {
                val totalCount = repository.getTotalProductCount()
                val activeCount = repository.getProductCountByStatus(ProductStatus.ACTIVE)
                val inactiveCount = repository.getProductCountByStatus(ProductStatus.INACTIVE)
                val draftCount = repository.getProductCountByStatus(ProductStatus.DRAFT)
                val outOfStockCount = repository.getProductCountByStatus(ProductStatus.OUT_OF_STOCK)
                val totalStock = repository.getTotalStock()
                
                val stats = ProductStats(
                    totalCount = totalCount,
                    activeCount = activeCount,
                    inactiveCount = inactiveCount,
                    draftCount = draftCount,
                    outOfStockCount = outOfStockCount,
                    totalStock = totalStock
                )
                
                statsLiveData.value = stats
                
            } catch (e: Exception) {
                _errorMessage.value = "获取统计信息失败：${e.message}"
            }
        }
        
        return statsLiveData
    }
    
    // 清除错误消息
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    data class ProductStats(
        val totalCount: Int,
        val activeCount: Int,
        val inactiveCount: Int,
        val draftCount: Int,
        val outOfStockCount: Int,
        val totalStock: Int
    )
}
