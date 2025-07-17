package com.productmanager.app.ui.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.productmanager.app.databinding.FragmentImageEditorBinding
import com.productmanager.app.utils.FileUtils
import com.productmanager.app.utils.ImageEditUtils
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageEditorFragment : Fragment() {
    
    private var _binding: FragmentImageEditorBinding? = null
    private val binding get() = _binding!!
    
    private val args: ImageEditorFragmentArgs by navArgs()
    private var currentBitmap: Bitmap? = null
    private var originalImagePath: String = ""
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageEditorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        originalImagePath = args.imagePath
        setupUI()
        loadImage()
        setupClickListeners()
    }
    
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun loadImage() {
        try {
            val imageFile = File(originalImagePath)
            if (imageFile.exists()) {
                currentBitmap = BitmapFactory.decodeFile(originalImagePath)
                binding.imageView.setImageBitmap(currentBitmap)
            } else {
                Toast.makeText(requireContext(), "图片文件不存在", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "加载图片失败：${e.message}", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    private fun setupClickListeners() {
        // 裁剪
        binding.btnCrop.setOnClickListener {
            startCrop()
        }
        
        // 旋转
        binding.btnRotate.setOnClickListener {
            rotateImage()
        }
        
        // 滤镜
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
        
        // 亮度调整
        binding.btnBrightness.setOnClickListener {
            showBrightnessDialog()
        }
        
        // 对比度调整
        binding.btnContrast.setOnClickListener {
            showContrastDialog()
        }
        
        // 添加水印
        binding.btnWatermark.setOnClickListener {
            showWatermarkDialog()
        }
        
        // 保存
        binding.btnSave.setOnClickListener {
            saveImage()
        }
        
        // 重置
        binding.btnReset.setOnClickListener {
            resetImage()
        }
    }
    
    private fun startCrop() {
        try {
            val sourceFile = File(originalImagePath)
            val destinationFile = File(
                requireContext().cacheDir,
                "cropped_${System.currentTimeMillis()}.jpg"
            )
            
            val sourceUri = Uri.fromFile(sourceFile)
            val destinationUri = Uri.fromFile(destinationFile)
            
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1920, 1080)
                .start(requireContext(), this)
                
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "启动裁剪失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun rotateImage() {
        currentBitmap?.let { bitmap ->
            val rotatedBitmap = ImageEditUtils.rotateBitmap(bitmap, 90f)
            currentBitmap = rotatedBitmap
            binding.imageView.setImageBitmap(rotatedBitmap)
        }
    }
    
    private fun showFilterDialog() {
        val filters = arrayOf("无滤镜", "黑白", "复古", "暖色调", "冷色调")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("选择滤镜")
            .setItems(filters) { _, which ->
                applyFilter(which)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun applyFilter(filterType: Int) {
        currentBitmap?.let { bitmap ->
            val filteredBitmap = when (filterType) {
                0 -> bitmap // 无滤镜
                1 -> ImageEditUtils.applyGrayscaleFilter(bitmap)
                2 -> ImageEditUtils.applyVintageFilter(bitmap)
                3 -> ImageEditUtils.applyWarmFilter(bitmap)
                4 -> ImageEditUtils.applyCoolFilter(bitmap)
                else -> bitmap
            }
            currentBitmap = filteredBitmap
            binding.imageView.setImageBitmap(filteredBitmap)
        }
    }
    
    private fun showBrightnessDialog() {
        val brightnessLevels = arrayOf("-50%", "-25%", "正常", "+25%", "+50%")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("调整亮度")
            .setItems(brightnessLevels) { _, which ->
                val brightness = when (which) {
                    0 -> -50f
                    1 -> -25f
                    2 -> 0f
                    3 -> 25f
                    4 -> 50f
                    else -> 0f
                }
                adjustBrightness(brightness)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun adjustBrightness(brightness: Float) {
        currentBitmap?.let { bitmap ->
            val adjustedBitmap = ImageEditUtils.adjustBrightness(bitmap, brightness)
            currentBitmap = adjustedBitmap
            binding.imageView.setImageBitmap(adjustedBitmap)
        }
    }
    
    private fun showContrastDialog() {
        val contrastLevels = arrayOf("低对比度", "正常", "高对比度", "超高对比度")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("调整对比度")
            .setItems(contrastLevels) { _, which ->
                val contrast = when (which) {
                    0 -> 0.5f
                    1 -> 1.0f
                    2 -> 1.5f
                    3 -> 2.0f
                    else -> 1.0f
                }
                adjustContrast(contrast)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun adjustContrast(contrast: Float) {
        currentBitmap?.let { bitmap ->
            val adjustedBitmap = ImageEditUtils.adjustContrast(bitmap, contrast)
            currentBitmap = adjustedBitmap
            binding.imageView.setImageBitmap(adjustedBitmap)
        }
    }
    
    private fun showWatermarkDialog() {
        val watermarkOptions = arrayOf("文字水印", "图片水印")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("添加水印")
            .setItems(watermarkOptions) { _, which ->
                when (which) {
                    0 -> showTextWatermarkDialog()
                    1 -> addImageWatermark()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showTextWatermarkDialog() {
        val input = android.widget.EditText(requireContext())
        input.hint = "输入水印文字"
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("文字水印")
            .setView(input)
            .setPositiveButton("添加") { _, _ ->
                val watermarkText = input.text.toString()
                if (watermarkText.isNotEmpty()) {
                    addTextWatermark(watermarkText)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun addTextWatermark(text: String) {
        currentBitmap?.let { bitmap ->
            val watermarkedBitmap = ImageEditUtils.addTextWatermark(bitmap, text)
            currentBitmap = watermarkedBitmap
            binding.imageView.setImageBitmap(watermarkedBitmap)
        }
    }
    
    private fun addImageWatermark() {
        // TODO: 实现图片水印功能
        Toast.makeText(requireContext(), "图片水印功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveImage() {
        currentBitmap?.let { bitmap ->
            try {
                val savedFile = ImageEditUtils.saveBitmapToFile(
                    requireContext(),
                    bitmap,
                    "edited_${System.currentTimeMillis()}.jpg"
                )
                
                if (savedFile != null) {
                    Toast.makeText(requireContext(), "图片已保存", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "保存失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun resetImage() {
        loadImage()
        Toast.makeText(requireContext(), "已重置为原图", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        currentBitmap?.recycle()
        _binding = null
    }
}
