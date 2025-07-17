package com.productmanager.app.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.productmanager.app.R
import com.productmanager.app.databinding.ItemProductImageBinding
import java.io.File

class ProductImageAdapter(
    private val onImageClick: (String) -> Unit,
    private val onImageDelete: (String) -> Unit,
    private val onSetMainImage: (String) -> Unit
) : ListAdapter<String, ProductImageAdapter.ImageViewHolder>(ImageDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemProductImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position), position == 0)
    }
    
    inner class ImageViewHolder(
        private val binding: ItemProductImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(imagePath: String, isMain: Boolean) {
            // 加载图片
            val imageFile = File(imagePath)
            Glide.with(binding.imageView.context)
                .load(imageFile)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageView)
            
            // 显示主图标记
            binding.iconMain.visibility = if (isMain) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            
            // 设置点击事件
            binding.imageView.setOnClickListener {
                onImageClick(imagePath)
            }
            
            binding.btnDelete.setOnClickListener {
                onImageDelete(imagePath)
            }
            
            binding.btnSetMain.setOnClickListener {
                onSetMainImage(imagePath)
            }
        }
    }
    
    private class ImageDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
