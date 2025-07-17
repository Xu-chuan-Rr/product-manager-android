package com.productmanager.app.ui.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.productmanager.app.R
import com.productmanager.app.data.entity.Product
import com.productmanager.app.data.entity.ProductStatus
import com.productmanager.app.databinding.ItemProductBinding
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onProductLongClick: (Product) -> Unit
) : ListAdapter<Product, ProductListAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)
        
        fun bind(product: Product) {
            binding.apply {
                // 商品标题
                textViewTitle.text = product.title
                
                // 商品描述
                if (product.description.isNullOrEmpty()) {
                    textViewDescription.visibility = View.GONE
                } else {
                    textViewDescription.visibility = View.VISIBLE
                    textViewDescription.text = product.description
                }
                
                // 价格
                textViewPrice.text = currencyFormat.format(product.price)
                
                // 原价（划线价）
                if (product.originalPrice != null && product.originalPrice > product.price) {
                    textViewOriginalPrice.visibility = View.VISIBLE
                    textViewOriginalPrice.text = currencyFormat.format(product.originalPrice)
                    textViewOriginalPrice.paintFlags = 
                        textViewOriginalPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textViewOriginalPrice.visibility = View.GONE
                }
                
                // 库存
                textViewStock.text = "库存: ${product.stock}"
                
                // 状态
                setupStatusChip(product.status)
                
                // 更新时间
                textViewUpdateTime.text = "更新: ${dateFormat.format(product.updatedAt)}"
                
                // 推荐标记
                iconRecommended.visibility = if (product.isRecommended) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
                // 销售数量
                if (product.salesCount > 0) {
                    textViewSalesCount.visibility = View.VISIBLE
                    textViewSalesCount.text = "已售: ${product.salesCount}"
                } else {
                    textViewSalesCount.visibility = View.GONE
                }
                
                // 商品图片 - 这里需要从数据库获取主图
                // TODO: 加载商品主图
                imageViewProduct.setImageResource(R.drawable.ic_image_placeholder)
                
                // 点击事件
                root.setOnClickListener {
                    onProductClick(product)
                }
                
                root.setOnLongClickListener {
                    onProductLongClick(product)
                    true
                }
            }
        }
        
        private fun setupStatusChip(status: ProductStatus) {
            binding.chipStatus.apply {
                text = when (status) {
                    ProductStatus.ACTIVE -> "正常"
                    ProductStatus.INACTIVE -> "下架"
                    ProductStatus.DRAFT -> "草稿"
                    ProductStatus.OUT_OF_STOCK -> "缺货"
                }
                
                val colorRes = when (status) {
                    ProductStatus.ACTIVE -> R.color.success_color
                    ProductStatus.INACTIVE -> R.color.text_secondary
                    ProductStatus.DRAFT -> R.color.warning_color
                    ProductStatus.OUT_OF_STOCK -> R.color.error_color
                }
                
                chipBackgroundColor = ContextCompat.getColorStateList(context, colorRes)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
    
    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
