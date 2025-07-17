package com.productmanager.app.ui.product

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.productmanager.app.ProductManagerApplication
import com.productmanager.app.databinding.FragmentAddProductBinding
import com.productmanager.app.ui.product.adapter.ProductImageAdapter
import com.productmanager.app.utils.CameraManager
import com.productmanager.app.utils.PermissionUtils
import java.io.File

class AddProductFragment : Fragment() {
    
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AddProductViewModel
    private lateinit var imageAdapter: ProductImageAdapter
    private lateinit var cameraManager: CameraManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupUI()
        setupImageRecyclerView()
        setupCameraManager()
        setupClickListeners()
        observeData()
    }
    
    private fun setupViewModel() {
        val application = requireActivity().application as ProductManagerApplication
        val factory = AddProductViewModelFactory(application.productRepository)
        viewModel = ViewModelProvider(this, factory)[AddProductViewModel::class.java]
    }
    
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // 设置分类下拉框
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
            )
            binding.autoCompleteCategory.setAdapter(adapter)
        }
    }
    
    private fun setupImageRecyclerView() {
        imageAdapter = ProductImageAdapter(
            onImageClick = { imagePath ->
                // 查看图片
                // TODO: 实现图片预览
            },
            onImageDelete = { imagePath ->
                viewModel.removeImage(imagePath)
            },
            onSetMainImage = { imagePath ->
                viewModel.setMainImage(imagePath)
            }
        )
        
        binding.recyclerViewImages.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }
    
    private fun setupCameraManager() {
        cameraManager = CameraManager(
            fragment = this,
            onImageCaptured = { file ->
                viewModel.addImage(file.absolutePath)
                Toast.makeText(requireContext(), "图片已添加", Toast.LENGTH_SHORT).show()
            },
            onVideoCaptured = { file ->
                // TODO: 处理视频
                Toast.makeText(requireContext(), "视频功能开发中", Toast.LENGTH_SHORT).show()
            },
            onImageSelected = { uri ->
                viewModel.addImageFromUri(uri)
                Toast.makeText(requireContext(), "图片已添加", Toast.LENGTH_SHORT).show()
            },
            onVideoSelected = { uri ->
                // TODO: 处理视频
                Toast.makeText(requireContext(), "视频功能开发中", Toast.LENGTH_SHORT).show()
            },
            onMultipleImagesSelected = { uris ->
                viewModel.addMultipleImagesFromUris(uris)
                Toast.makeText(requireContext(), "已添加 ${uris.size} 张图片", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    private fun setupClickListeners() {
        // 添加图片
        binding.btnAddImage.setOnClickListener {
            if (PermissionUtils.hasAllPermissions(requireContext())) {
                cameraManager.showImagePickerDialog()
            } else {
                requestPermissions()
            }
        }
        
        // 保存商品
        binding.btnSave.setOnClickListener {
            saveProduct()
        }
        
        // 保存为草稿
        binding.btnSaveDraft.setOnClickListener {
            saveProductAsDraft()
        }
    }
    
    private fun observeData() {
        viewModel.images.observe(viewLifecycleOwner) { images ->
            imageAdapter.submitList(images)
            
            // 更新添加图片按钮的可见性
            binding.btnAddImage.text = if (images.isEmpty()) {
                "添加图片"
            } else {
                "添加更多图片 (${images.size})"
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
            binding.btnSaveDraft.isEnabled = !isLoading
        }
        
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(requireContext(), "商品保存成功", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "保存失败：${it.exceptionOrNull()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.clearSaveResult()
            }
        }
    }
    
    private fun saveProduct() {
        if (validateInput()) {
            val productData = collectProductData()
            viewModel.saveProduct(productData, false)
        }
    }
    
    private fun saveProductAsDraft() {
        val productData = collectProductData()
        viewModel.saveProduct(productData, true)
    }
    
    private fun validateInput(): Boolean {
        var isValid = true
        
        // 验证商品标题
        if (binding.editTextTitle.text.toString().trim().isEmpty()) {
            binding.textInputTitle.error = "请输入商品标题"
            isValid = false
        } else {
            binding.textInputTitle.error = null
        }
        
        // 验证商品价格
        val priceText = binding.editTextPrice.text.toString().trim()
        if (priceText.isEmpty()) {
            binding.textInputPrice.error = "请输入商品价格"
            isValid = false
        } else {
            try {
                val price = priceText.toDouble()
                if (price < 0) {
                    binding.textInputPrice.error = "价格不能为负数"
                    isValid = false
                } else {
                    binding.textInputPrice.error = null
                }
            } catch (e: NumberFormatException) {
                binding.textInputPrice.error = "请输入有效的价格"
                isValid = false
            }
        }
        
        // 验证库存
        val stockText = binding.editTextStock.text.toString().trim()
        if (stockText.isEmpty()) {
            binding.textInputStock.error = "请输入库存数量"
            isValid = false
        } else {
            try {
                val stock = stockText.toInt()
                if (stock < 0) {
                    binding.textInputStock.error = "库存不能为负数"
                    isValid = false
                } else {
                    binding.textInputStock.error = null
                }
            } catch (e: NumberFormatException) {
                binding.textInputStock.error = "请输入有效的库存数量"
                isValid = false
            }
        }
        
        return isValid
    }
    
    private fun collectProductData(): AddProductViewModel.ProductData {
        return AddProductViewModel.ProductData(
            title = binding.editTextTitle.text.toString().trim(),
            description = binding.editTextDescription.text.toString().trim(),
            price = binding.editTextPrice.text.toString().trim().toDoubleOrNull() ?: 0.0,
            originalPrice = binding.editTextOriginalPrice.text.toString().trim().toDoubleOrNull(),
            stock = binding.editTextStock.text.toString().trim().toIntOrNull() ?: 0,
            category = binding.autoCompleteCategory.text.toString().trim(),
            tags = binding.editTextTags.text.toString().trim(),
            sku = binding.editTextSku.text.toString().trim(),
            barcode = binding.editTextBarcode.text.toString().trim(),
            weight = binding.editTextWeight.text.toString().trim().toDoubleOrNull(),
            dimensions = binding.editTextDimensions.text.toString().trim(),
            material = binding.editTextMaterial.text.toString().trim(),
            brand = binding.editTextBrand.text.toString().trim(),
            isRecommended = binding.switchRecommended.isChecked
        )
    }
    
    private fun requestPermissions() {
        PermissionUtils.requestPermissions(
            activity = requireActivity(),
            onPermissionsGranted = {
                Toast.makeText(requireContext(), "权限已授予", Toast.LENGTH_SHORT).show()
            },
            onPermissionsDenied = { deniedPermissions ->
                val permissionNames = deniedPermissions.map { PermissionUtils.getPermissionName(it) }
                Toast.makeText(
                    requireContext(),
                    "需要权限：${permissionNames.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
            },
            onPermissionsPermanentlyDenied = { _ ->
                Toast.makeText(requireContext(), "请在设置中开启权限", Toast.LENGTH_LONG).show()
                PermissionUtils.openAppSettings(requireContext())
            }
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
