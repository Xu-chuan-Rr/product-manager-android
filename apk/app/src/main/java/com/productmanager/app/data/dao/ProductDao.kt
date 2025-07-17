package com.productmanager.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.productmanager.app.data.entity.Product
import com.productmanager.app.data.entity.ProductStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProductsLiveData(): LiveData<List<Product>>
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?
    
    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY sortOrder ASC, updatedAt DESC")
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE status = :status ORDER BY updatedAt DESC")
    fun getProductsByStatus(status: ProductStatus): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE isRecommended = 1 ORDER BY sortOrder ASC, updatedAt DESC")
    fun getRecommendedProducts(): Flow<List<Product>>
    
    @Query("""
        SELECT * FROM products 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR tags LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchProducts(query: String): Flow<List<Product>>
    
    @Query("""
        SELECT * FROM products 
        WHERE (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:status IS NULL OR status = :status)
        AND (:tags IS NULL OR tags LIKE '%' || :tags || '%')
        ORDER BY 
        CASE WHEN :sortBy = 'title' THEN title END ASC,
        CASE WHEN :sortBy = 'price' THEN price END ASC,
        CASE WHEN :sortBy = 'stock' THEN stock END ASC,
        CASE WHEN :sortBy = 'created' THEN createdAt END DESC,
        updatedAt DESC
    """)
    fun getFilteredProducts(
        categoryId: Long? = null,
        status: ProductStatus? = null,
        tags: String? = null,
        sortBy: String = "updated"
    ): Flow<List<Product>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>): List<Long>
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)
    
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity")
    suspend fun decreaseStock(id: Long, quantity: Int): Int
    
    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :id")
    suspend fun increaseStock(id: Long, quantity: Int)
    
    @Query("UPDATE products SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: Long)
    
    @Query("UPDATE products SET salesCount = salesCount + :quantity WHERE id = :id")
    suspend fun incrementSalesCount(id: Long, quantity: Int)
    
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getTotalProductCount(): Int
    
    @Query("SELECT COUNT(*) FROM products WHERE status = :status")
    suspend fun getProductCountByStatus(status: ProductStatus): Int
    
    @Query("SELECT SUM(stock) FROM products WHERE status = 'ACTIVE'")
    suspend fun getTotalStock(): Int?
}
