package com.productmanager.app.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.productmanager.app.ProductManagerApplication
import com.productmanager.app.R
import com.productmanager.app.databinding.FragmentGalleryBinding
import com.productmanager.app.ui.gallery.adapter.GalleryAdapter
import com.productmanager.app.utils.CameraManager
import com.productmanager.app.utils.FileUtils
import com.productmanager.app.utils.PermissionUtils
import java.io.File

class GalleryFragment : Fragment() {
    
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: GalleryViewModel
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var cameraManager: CameraManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupCameraManager()
        setupClickListeners()
        observeData()
    }
    
    private fun setupViewModel() {
        val application = requireActivity().application as ProductManagerApplication
        val factory = GalleryViewModelFactory(application.productRepository)
        viewModel = ViewModelProvider(this, factory)[GalleryViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter(
            onImageClick = { image ->
                // 点击图片查看详情
                val action = GalleryFragmentDirections.actionGalleryToImageEditor(image.imagePath)
                findNavController().navigate(action)
            },
            onImageLongClick = { image ->
                // 长按显示操作菜单
                showImageOptionsDialog(image.id)
            }
        )
        
        binding.recyclerViewGallery.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = galleryAdapter
        }
    }
    
    private fun setupCameraManager() {
        cameraManager = CameraManager(
            fragment = this,
            onImageCaptured = { file ->
                handleImageCaptured(file)
            },
            onVideoCaptured = { file ->
                handleVideoCaptured(file)
            },
            onImageSelected = { uri ->
                handleImageSelected(uri)
            },
            onVideoSelected = { uri ->
                handleVideoSelected(uri)
            },
            onMultipleImagesSelected = { uris ->
                handleMultipleImagesSelected(uris)
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    private fun setupClickListeners() {
        binding.fabAddImage.setOnClickListener {
            if (PermissionUtils.hasAllPermissions(requireContext())) {
                cameraManager.showImagePickerDialog()
            } else {
                requestPermissions()
            }
        }
        
        binding.fabAddVideo.setOnClickListener {
            if (PermissionUtils.hasAllPermissions(requireContext())) {
                cameraManager.showVideoPickerDialog()
            } else {
                requestPermissions()
            }
        }
    }
    
    private fun observeData() {
        viewModel.allImages.observe(viewLifecycleOwner) { images ->
            galleryAdapter.submitList(images)
            
            // 更新UI状态
            if (images.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewGallery.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewGallery.visibility = View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun handleImageCaptured(file: File) {
        viewModel.addImageFromFile(file)
        Toast.makeText(requireContext(), "图片已保存", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleVideoCaptured(file: File) {
        // TODO: 实现视频处理逻辑
        Toast.makeText(requireContext(), "视频已保存", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleImageSelected(uri: Uri) {
        viewModel.addImageFromUri(uri)
        Toast.makeText(requireContext(), "图片已添加", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleVideoSelected(uri: Uri) {
        // TODO: 实现视频处理逻辑
        Toast.makeText(requireContext(), "视频已添加", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleMultipleImagesSelected(uris: List<Uri>) {
        viewModel.addMultipleImagesFromUris(uris)
        Toast.makeText(requireContext(), "已添加 ${uris.size} 张图片", Toast.LENGTH_SHORT).show()
    }
    
    private fun showImageOptionsDialog(imageId: Long) {
        val options = arrayOf("编辑", "设为主图", "删除")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("图片操作")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // 编辑图片
                        viewModel.getImageById(imageId)?.let { image ->
                            val action = GalleryFragmentDirections.actionGalleryToImageEditor(image.imagePath)
                            findNavController().navigate(action)
                        }
                    }
                    1 -> {
                        // 设为主图
                        viewModel.setAsMainImage(imageId)
                        Toast.makeText(requireContext(), "已设为主图", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        // 删除图片
                        showDeleteConfirmDialog(imageId)
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showDeleteConfirmDialog(imageId: Long) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("删除图片")
            .setMessage("确定要删除这张图片吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteImage(imageId)
                Toast.makeText(requireContext(), "图片已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
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
