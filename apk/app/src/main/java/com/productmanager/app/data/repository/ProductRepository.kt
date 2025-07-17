package com.productmanager.app.data.repository

import androidx.lifecycle.LiveData
import com.productmanager.app.data.dao.CategoryDao
import com.productmanager.app.data.dao.ProductDao
import com.productmanager.app.data.dao.ProductImageDao
import com.productmanager.app.data.entity.*
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val productImageDao: ProductImageDao
) {
    
    // Product operations
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    
    fun getAllProductsLiveData(): LiveData<List<Product>> = productDao.getAllProductsLiveData()
    
    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)
    
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> = 
        productDao.getProductsByCategory(categoryId)
    
    fun getProductsByStatus(status: ProductStatus): Flow<List<Product>> = 
        productDao.getProductsByStatus(status)
    
    fun getRecommendedProducts(): Flow<List<Product>> = productDao.getRecommendedProducts()
    
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    
    fun getFilteredProducts(
        categoryId: Long? = null,
        status: ProductStatus? = null,
        tags: String? = null,
        sortBy: String = "updated"
    ): Flow<List<Product>> = productDao.getFilteredProducts(categoryId, status, tags, sortBy)
    
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    
    suspend fun deleteProductById(id: Long) = productDao.deleteProductById(id)
    
    suspend fun decreaseStock(id: Long, quantity: Int): Int = 
        productDao.decreaseStock(id, quantity)
    
    suspend fun increaseStock(id: Long, quantity: Int) = productDao.increaseStock(id, quantity)
    
    suspend fun incrementViewCount(id: Long) = productDao.incrementViewCount(id)
    
    suspend fun incrementSalesCount(id: Long, quantity: Int) = 
        productDao.incrementSalesCount(id, quantity)
    
    suspend fun getTotalProductCount(): Int = productDao.getTotalProductCount()
    
    suspend fun getProductCountByStatus(status: ProductStatus): Int = 
        productDao.getProductCountByStatus(status)
    
    suspend fun getTotalStock(): Int = productDao.getTotalStock() ?: 0
    
    // Category operations
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    fun getAllCategoriesLiveData(): LiveData<List<Category>> = categoryDao.getAllCategoriesLiveData()
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    
    suspend fun getCategoryByName(name: String): Category? = categoryDao.getCategoryByName(name)
    
    suspend fun getProductCountByCategory(categoryId: Long): Int = 
        categoryDao.getProductCountByCategory(categoryId)
    
    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    
    suspend fun deactivateCategory(id: Long) = categoryDao.deactivateCategory(id)
    
    fun searchCategories(query: String): Flow<List<Category>> = 
        categoryDao.searchCategories(query)
    
    // ProductImage operations
    fun getImagesByProductId(productId: Long): Flow<List<ProductImage>> = 
        productImageDao.getImagesByProductId(productId)
    
    fun getImagesByProductIdLiveData(productId: Long): LiveData<List<ProductImage>> = 
        productImageDao.getImagesByProductIdLiveData(productId)
    
    suspend fun getMainImageByProductId(productId: Long): ProductImage? = 
        productImageDao.getMainImageByProductId(productId)
    
    fun getImagesByProductIdAndType(productId: Long, type: ImageType): Flow<List<ProductImage>> = 
        productImageDao.getImagesByProductIdAndType(productId, type)
    
    suspend fun getImageById(id: Long): ProductImage? = productImageDao.getImageById(id)
    
    suspend fun insertImage(image: ProductImage): Long = productImageDao.insertImage(image)
    
    suspend fun insertImages(images: List<ProductImage>): List<Long> = 
        productImageDao.insertImages(images)
    
    suspend fun updateImage(image: ProductImage) = productImageDao.updateImage(image)
    
    suspend fun deleteImage(image: ProductImage) = productImageDao.deleteImage(image)
    
    suspend fun deleteImageById(id: Long) = productImageDao.deleteImageById(id)
    
    suspend fun deleteImagesByProductId(productId: Long) = 
        productImageDao.deleteImagesByProductId(productId)
    
    suspend fun setMainImage(productId: Long, imageId: Long) = 
        productImageDao.setMainImage(productId, imageId)
    
    suspend fun getImageCountByProductId(productId: Long): Int = 
        productImageDao.getImageCountByProductId(productId)
    
    fun getRecentImages(limit: Int): Flow<List<ProductImage>> = 
        productImageDao.getRecentImages(limit)
}
