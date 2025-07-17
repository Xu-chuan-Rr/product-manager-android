package com.productmanager.app.ui.product

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.productmanager.app.ProductManagerApplication
import com.productmanager.app.data.entity.Category
import com.productmanager.app.data.entity.ImageType
import com.productmanager.app.data.entity.Product
import com.productmanager.app.data.entity.ProductImage
import com.productmanager.app.data.entity.ProductStatus
import com.productmanager.app.data.repository.ProductRepository
import com.productmanager.app.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.math.BigDecimal
import java.util.Date

class AddProductViewModel(private val repository: ProductRepository) : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _saveResult = MutableLiveData<Result<Long>?>()
    val saveResult: LiveData<Result<Long>?> = _saveResult
    
    private val _images = MutableLiveData<List<String>>(emptyList())
    val images: LiveData<List<String>> = _images
    
    private var mainImagePath: String? = null
    
    // 获取所有分类
    val categories: LiveData<List<Category>> = repository.getAllCategories().asLiveData()
    
    // 添加图片
    fun addImage(imagePath: String) {
        val currentImages = _images.value?.toMutableList() ?: mutableListOf()
        currentImages.add(imagePath)
        _images.value = currentImages
        
        // 如果是第一张图片，设为主图
        if (mainImagePath == null) {
            mainImagePath = imagePath
        }
    }
    
    // 从Uri添加图片
    fun addImageFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = ProductManagerApplication.instance
                val targetFile = FileUtils.createImageFile(context)
                val success = FileUtils.copyFileToAppDirectory(context, uri, targetFile)
                
                if (success) {
                    addImage(targetFile.absolutePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // 批量添加图片
    fun addMultipleImagesFromUris(uris: List<Uri>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val context = ProductManagerApplication.instance
                uris.forEach { uri ->
                    try {
                        val targetFile = FileUtils.createImageFile(context)
                        val success = FileUtils.copyFileToAppDirectory(context, uri, targetFile)
                        
                        if (success) {
                            withContext(Dispatchers.Main) {
                                addImage(targetFile.absolutePath)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    
    // 移除图片
    fun removeImage(imagePath: String) {
        val currentImages = _images.value?.toMutableList() ?: mutableListOf()
        currentImages.remove(imagePath)
        _images.value = currentImages
        
        // 如果移除的是主图，重新设置主图
        if (mainImagePath == imagePath) {
            mainImagePath = currentImages.firstOrNull()
        }
        
        // 删除文件
        try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // 设置主图
    fun setMainImage(imagePath: String) {
        mainImagePath = imagePath
    }
    
    // 保存商品
    fun saveProduct(productData: ProductData, isDraft: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 获取或创建分类
                val categoryId = if (productData.category.isNotEmpty()) {
                    val existingCategory = repository.getCategoryByName(productData.category)
                    existingCategory?.id ?: run {
                        val newCategory = Category(
                            name = productData.category,
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        repository.insertCategory(newCategory)
                    }
                } else {
                    null
                }
                
                // 创建商品
                val product = Product(
                    title = productData.title,
                    description = productData.description.ifEmpty { null },
                    price = BigDecimal(productData.price),
                    originalPrice = productData.originalPrice?.let { BigDecimal(it) },
                    stock = productData.stock,
                    categoryId = categoryId,
                    tags = productData.tags.ifEmpty { null },
                    sku = productData.sku.ifEmpty { null },
                    barcode = productData.barcode.ifEmpty { null },
                    weight = productData.weight,
                    dimensions = productData.dimensions.ifEmpty { null },
                    material = productData.material.ifEmpty { null },
                    brand = productData.brand.ifEmpty { null },
                    status = if (isDraft) ProductStatus.DRAFT else ProductStatus.ACTIVE,
                    isRecommended = productData.isRecommended,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                val productId = repository.insertProduct(product)
                
                // 保存图片
                val imageList = _images.value ?: emptyList()
                imageList.forEachIndexed { index, imagePath ->
                    val context = ProductManagerApplication.instance
                    val thumbnailFile = FileUtils.createThumbnail(context, File(imagePath))
                    
                    val productImage = ProductImage(
                        productId = productId,
                        imagePath = imagePath,
                        thumbnailPath = thumbnailFile?.absolutePath,
                        imageType = ImageType.PRODUCT,
                        sortOrder = index,
                        isMain = imagePath == mainImagePath,
                        fileSize = FileUtils.getFileSize(File(imagePath)),
                        mimeType = FileUtils.getMimeType(File(imagePath)),
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                    
                    repository.insertImage(productImage)
                }
                
                _saveResult.value = Result.success(productId)
                
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 清除保存结果
    fun clearSaveResult() {
        _saveResult.value = null
    }
    
    data class ProductData(
        val title: String,
        val description: String,
        val price: Double,
        val originalPrice: Double?,
        val stock: Int,
        val category: String,
        val tags: String,
        val sku: String,
        val barcode: String,
        val weight: Double?,
        val dimensions: String,
        val material: String,
        val brand: String,
        val isRecommended: Boolean
    )
}
