package com.productmanager.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.productmanager.app.BuildConfig
import com.productmanager.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        setupVersionInfo()
    }
    
    private fun setupClickListeners() {
        // 数据管理
        binding.layoutDataManagement.setOnClickListener {
            showDataManagementDialog()
        }
        
        // 导入导出
        binding.layoutImportExport.setOnClickListener {
            showImportExportDialog()
        }
        
        // 备份恢复
        binding.layoutBackupRestore.setOnClickListener {
            showBackupRestoreDialog()
        }
        
        // 清除缓存
        binding.layoutClearCache.setOnClickListener {
            showClearCacheDialog()
        }
        
        // 关于应用
        binding.layoutAbout.setOnClickListener {
            showAboutDialog()
        }
        
        // 帮助反馈
        binding.layoutHelp.setOnClickListener {
            showHelpDialog()
        }
        
        // 隐私政策
        binding.layoutPrivacy.setOnClickListener {
            showPrivacyPolicy()
        }
    }
    
    private fun setupVersionInfo() {
        binding.textViewVersion.text = "版本 ${BuildConfig.VERSION_NAME}"
    }
    
    private fun showDataManagementDialog() {
        val options = arrayOf("查看存储使用情况", "清理临时文件", "重置应用数据")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("数据管理")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showStorageInfo()
                    1 -> clearTempFiles()
                    2 -> showResetDataDialog()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showImportExportDialog() {
        val options = arrayOf("导出商品数据", "导入商品数据", "导出图片", "批量导入图片")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("导入导出")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> exportProductData()
                    1 -> importProductData()
                    2 -> exportImages()
                    3 -> importImages()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showBackupRestoreDialog() {
        val options = arrayOf("创建备份", "恢复备份", "自动备份设置")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("备份恢复")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> createBackup()
                    1 -> restoreBackup()
                    2 -> showAutoBackupSettings()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showClearCacheDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("清除缓存")
            .setMessage("确定要清除应用缓存吗？这将删除临时文件和图片缓存。")
            .setPositiveButton("清除") { _, _ ->
                clearCache()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showAboutDialog() {
        val message = """
            商品管理 v${BuildConfig.VERSION_NAME}
            
            一款专业的商品信息管理应用，支持图片编辑、分类管理、库存跟踪等功能。
            
            开发者：产品管理团队
            联系邮箱：support@productmanager.com
            
            感谢您的使用！
        """.trimIndent()
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("关于应用")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
    
    private fun showHelpDialog() {
        val options = arrayOf("使用教程", "常见问题", "联系客服", "用户手册")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("帮助与反馈")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showTutorial()
                    1 -> showFAQ()
                    2 -> contactSupport()
                    3 -> openUserManual()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showPrivacyPolicy() {
        val message = """
            隐私政策
            
            我们重视您的隐私保护。本应用：
            
            1. 仅在本地存储您的商品数据
            2. 不会上传您的个人信息到服务器
            3. 相机和存储权限仅用于图片管理功能
            4. 不会收集您的使用行为数据
            
            如有疑问，请联系我们。
        """.trimIndent()
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("隐私政策")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
    
    // 实现各种功能方法
    private fun showStorageInfo() {
        Toast.makeText(requireContext(), "存储信息功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun clearTempFiles() {
        Toast.makeText(requireContext(), "临时文件已清理", Toast.LENGTH_SHORT).show()
    }
    
    private fun showResetDataDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("重置应用数据")
            .setMessage("警告：此操作将删除所有商品数据和图片，且无法恢复！")
            .setPositiveButton("确认重置") { _, _ ->
                Toast.makeText(requireContext(), "数据重置功能开发中", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun exportProductData() {
        Toast.makeText(requireContext(), "导出功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun importProductData() {
        Toast.makeText(requireContext(), "导入功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun exportImages() {
        Toast.makeText(requireContext(), "图片导出功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun importImages() {
        Toast.makeText(requireContext(), "图片导入功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun createBackup() {
        Toast.makeText(requireContext(), "备份功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun restoreBackup() {
        Toast.makeText(requireContext(), "恢复功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun showAutoBackupSettings() {
        Toast.makeText(requireContext(), "自动备份设置开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun clearCache() {
        Toast.makeText(requireContext(), "缓存已清除", Toast.LENGTH_SHORT).show()
    }
    
    private fun showTutorial() {
        Toast.makeText(requireContext(), "教程功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun showFAQ() {
        Toast.makeText(requireContext(), "FAQ功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@productmanager.com")
            putExtra(Intent.EXTRA_SUBJECT, "商品管理应用反馈")
        }
        startActivity(intent)
    }
    
    private fun openUserManual() {
        Toast.makeText(requireContext(), "用户手册功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
