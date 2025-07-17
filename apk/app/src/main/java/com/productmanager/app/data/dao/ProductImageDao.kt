package com.productmanager.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.productmanager.app.data.entity.ImageType
import com.productmanager.app.data.entity.ProductImage
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductImageDao {
    
    @Query("SELECT * FROM product_images WHERE productId = :productId ORDER BY sortOrder ASC, createdAt ASC")
    fun getImagesByProductId(productId: Long): Flow<List<ProductImage>>
    
    @Query("SELECT * FROM product_images WHERE productId = :productId ORDER BY sortOrder ASC, createdAt ASC")
    fun getImagesByProductIdLiveData(productId: Long): LiveData<List<ProductImage>>
    
    @Query("SELECT * FROM product_images WHERE productId = :productId AND isMain = 1 LIMIT 1")
    suspend fun getMainImageByProductId(productId: Long): ProductImage?
    
    @Query("SELECT * FROM product_images WHERE productId = :productId AND imageType = :type ORDER BY sortOrder ASC")
    fun getImagesByProductIdAndType(productId: Long, type: ImageType): Flow<List<ProductImage>>
    
    @Query("SELECT * FROM product_images WHERE id = :id")
    suspend fun getImageById(id: Long): ProductImage?
    
    @Query("SELECT * FROM product_images WHERE imagePath = :path")
    suspend fun getImageByPath(path: String): ProductImage?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ProductImage): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ProductImage>): List<Long>
    
    @Update
    suspend fun updateImage(image: ProductImage)
    
    @Delete
    suspend fun deleteImage(image: ProductImage)
    
    @Query("DELETE FROM product_images WHERE id = :id")
    suspend fun deleteImageById(id: Long)
    
    @Query("DELETE FROM product_images WHERE productId = :productId")
    suspend fun deleteImagesByProductId(productId: Long)
    
    @Query("UPDATE product_images SET isMain = 0 WHERE productId = :productId")
    suspend fun clearMainImageForProduct(productId: Long)
    
    @Query("UPDATE product_images SET isMain = 1 WHERE id = :imageId")
    suspend fun setAsMainImage(imageId: Long)
    
    @Transaction
    suspend fun setMainImage(productId: Long, imageId: Long) {
        clearMainImageForProduct(productId)
        setAsMainImage(imageId)
    }
    
    @Query("SELECT COUNT(*) FROM product_images WHERE productId = :productId")
    suspend fun getImageCountByProductId(productId: Long): Int
    
    @Query("SELECT * FROM product_images ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentImages(limit: Int): Flow<List<ProductImage>>
}
