package com.productmanager.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.productmanager.app.data.entity.ProductVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVideoDao {
    
    @Query("SELECT * FROM product_videos WHERE productId = :productId ORDER BY sortOrder ASC, createdAt ASC")
    fun getVideosByProductId(productId: Long): Flow<List<ProductVideo>>
    
    @Query("SELECT * FROM product_videos WHERE productId = :productId ORDER BY sortOrder ASC, createdAt ASC")
    fun getVideosByProductIdLiveData(productId: Long): LiveData<List<ProductVideo>>
    
    @Query("SELECT * FROM product_videos WHERE productId = :productId AND isMain = 1 LIMIT 1")
    suspend fun getMainVideoByProductId(productId: Long): ProductVideo?
    
    @Query("SELECT * FROM product_videos WHERE id = :id")
    suspend fun getVideoById(id: Long): ProductVideo?
    
    @Query("SELECT * FROM product_videos WHERE videoPath = :path")
    suspend fun getVideoByPath(path: String): ProductVideo?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: ProductVideo): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<ProductVideo>): List<Long>
    
    @Update
    suspend fun updateVideo(video: ProductVideo)
    
    @Delete
    suspend fun deleteVideo(video: ProductVideo)
    
    @Query("DELETE FROM product_videos WHERE id = :id")
    suspend fun deleteVideoById(id: Long)
    
    @Query("DELETE FROM product_videos WHERE productId = :productId")
    suspend fun deleteVideosByProductId(productId: Long)
    
    @Query("UPDATE product_videos SET isMain = 0 WHERE productId = :productId")
    suspend fun clearMainVideoForProduct(productId: Long)
    
    @Query("UPDATE product_videos SET isMain = 1 WHERE id = :videoId")
    suspend fun setAsMainVideo(videoId: Long)
    
    @Transaction
    suspend fun setMainVideo(productId: Long, videoId: Long) {
        clearMainVideoForProduct(productId)
        setAsMainVideo(videoId)
    }
    
    @Query("SELECT COUNT(*) FROM product_videos WHERE productId = :productId")
    suspend fun getVideoCountByProductId(productId: Long): Int
    
    @Query("SELECT * FROM product_videos ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentVideos(limit: Int): Flow<List<ProductVideo>>
}
