package com.productmanager.app.ui.gallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.productmanager.app.R
import com.productmanager.app.data.entity.ProductImage
import com.productmanager.app.databinding.ItemGalleryImageBinding
import java.io.File

class GalleryAdapter(
    private val onImageClick: (ProductImage) -> Unit,
    private val onImageLongClick: (ProductImage) -> Unit
) : ListAdapter<ProductImage, GalleryAdapter.ImageViewHolder>(ImageDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemGalleryImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ImageViewHolder(
        private val binding: ItemGalleryImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(image: ProductImage) {
            // 加载图片
            val imagePath = image.thumbnailPath ?: image.imagePath
            val imageFile = File(imagePath)
            
            Glide.with(binding.imageView.context)
                .load(imageFile)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageView)
            
            // 显示主图标记
            binding.iconMain.visibility = if (image.isMain) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            
            // 显示水印标记
            binding.iconWatermark.visibility = if (image.watermarkApplied) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            
            // 设置点击事件
            binding.root.setOnClickListener {
                onImageClick(image)
            }
            
            binding.root.setOnLongClickListener {
                onImageLongClick(image)
                true
            }
        }
    }
    
    private class ImageDiffCallback : DiffUtil.ItemCallback<ProductImage>() {
        override fun areItemsTheSame(oldItem: ProductImage, newItem: ProductImage): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ProductImage, newItem: ProductImage): Boolean {
            return oldItem == newItem
        }
    }
}
