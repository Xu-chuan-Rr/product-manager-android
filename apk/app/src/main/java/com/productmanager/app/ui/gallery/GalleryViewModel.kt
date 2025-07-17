package com.productmanager.app.ui.gallery

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.productmanager.app.ProductManagerApplication
import com.productmanager.app.data.entity.ImageType
import com.productmanager.app.data.entity.ProductImage
import com.productmanager.app.data.repository.ProductRepository
import com.productmanager.app.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class GalleryViewModel(private val repository: ProductRepository) : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    // 获取所有图片
    val allImages: LiveData<List<ProductImage>> = repository.getRecentImages(1000).asLiveData()
    
    // 当前选中的图片
    private val _selectedImages = MutableLiveData<List<ProductImage>>()
    val selectedImages: LiveData<List<ProductImage>> = _selectedImages
    
    // 从文件添加图片
    fun addImageFromFile(file: File) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val context = ProductManagerApplication.instance
                
                // 创建缩略图
                val thumbnailFile = FileUtils.createThumbnail(context, file)
                
                // 创建ProductImage实体
                val productImage = ProductImage(
                    productId = 0, // 暂时设为0，表示未关联商品
                    imagePath = file.absolutePath,
                    thumbnailPath = thumbnailFile?.absolutePath,
                    imageType = ImageType.PRODUCT,
                    width = null, // TODO: 获取图片尺寸
                    height = null,
                    fileSize = FileUtils.getFileSize(file),
                    mimeType = FileUtils.getMimeType(file),
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                repository.insertImage(productImage)
                
            } catch (e: Exception) {
                _errorMessage.value = "添加图片失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 从Uri添加图片
    fun addImageFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val context = ProductManagerApplication.instance
                
                // 创建目标文件
                val targetFile = FileUtils.createImageFile(context)
                
                // 复制文件到应用目录
                val success = FileUtils.copyFileToAppDirectory(context, uri, targetFile)
                
                if (success) {
                    addImageFromFile(targetFile)
                } else {
                    _errorMessage.value = "复制图片失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "添加图片失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 批量添加图片
    fun addMultipleImagesFromUris(uris: List<Uri>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val context = ProductManagerApplication.instance
                val images = mutableListOf<ProductImage>()
                
                withContext(Dispatchers.IO) {
                    uris.forEach { uri ->
                        try {
                            val targetFile = FileUtils.createImageFile(context)
                            val success = FileUtils.copyFileToAppDirectory(context, uri, targetFile)
                            
                            if (success) {
                                val thumbnailFile = FileUtils.createThumbnail(context, targetFile)
                                
                                val productImage = ProductImage(
                                    productId = 0,
                                    imagePath = targetFile.absolutePath,
                                    thumbnailPath = thumbnailFile?.absolutePath,
                                    imageType = ImageType.PRODUCT,
                                    fileSize = FileUtils.getFileSize(targetFile),
                                    mimeType = FileUtils.getMimeType(targetFile),
                                    createdAt = Date(),
                                    updatedAt = Date()
                                )
                                
                                images.add(productImage)
                            }
                        } catch (e: Exception) {
                            // 记录错误但继续处理其他图片
                            e.printStackTrace()
                        }
                    }
                }
                
                if (images.isNotEmpty()) {
                    repository.insertImages(images)
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "批量添加图片失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 删除图片
    fun deleteImage(imageId: Long) {
        viewModelScope.launch {
            try {
                val image = repository.getImageById(imageId)
                image?.let {
                    // 删除文件
                    val file = File(it.imagePath)
                    FileUtils.deleteFile(file)
                    
                    // 删除缩略图
                    it.thumbnailPath?.let { thumbnailPath ->
                        val thumbnailFile = File(thumbnailPath)
                        FileUtils.deleteFile(thumbnailFile)
                    }
                    
                    // 从数据库删除
                    repository.deleteImageById(imageId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "删除图片失败：${e.message}"
            }
        }
    }
    
    // 设为主图
    fun setAsMainImage(imageId: Long) {
        viewModelScope.launch {
            try {
                val image = repository.getImageById(imageId)
                image?.let {
                    if (it.productId > 0) {
                        repository.setMainImage(it.productId, imageId)
                    } else {
                        // 如果图片未关联商品，更新为主图标记
                        val updatedImage = it.copy(isMain = true, updatedAt = Date())
                        repository.updateImage(updatedImage)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "设置主图失败：${e.message}"
            }
        }
    }
    
    // 根据ID获取图片
    fun getImageById(imageId: Long): ProductImage? {
        return allImages.value?.find { it.id == imageId }
    }
    
    // 搜索图片
    fun searchImages(query: String): LiveData<List<ProductImage>> {
        // TODO: 实现图片搜索功能
        return allImages
    }
    
    // 按类型筛选图片
    fun filterImagesByType(type: ImageType): LiveData<List<ProductImage>> {
        // TODO: 实现按类型筛选功能
        return allImages
    }
    
    // 清除错误消息
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
