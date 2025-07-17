package com.productmanager.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.productmanager.app.R
import com.productmanager.app.databinding.ActivityMainBinding
import com.productmanager.app.utils.PermissionUtils

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        checkPermissions()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
    
    private fun checkPermissions() {
        if (!PermissionUtils.hasAllPermissions(this)) {
            PermissionUtils.requestPermissions(
                activity = this,
                onPermissionsGranted = {
                    // 权限已授予，可以继续使用应用
                },
                onPermissionsDenied = { deniedPermissions ->
                    // 权限被拒绝，显示说明
                    showPermissionDeniedDialog(deniedPermissions)
                },
                onPermissionsPermanentlyDenied = { permanentlyDeniedPermissions ->
                    // 权限被永久拒绝，引导用户到设置页面
                    showPermissionPermanentlyDeniedDialog(permanentlyDeniedPermissions)
                }
            )
        }
    }
    
    private fun showPermissionDeniedDialog(deniedPermissions: List<String>) {
        val permissionNames = deniedPermissions.map { PermissionUtils.getPermissionName(it) }
        val message = "应用需要以下权限才能正常工作：\n${permissionNames.joinToString("\n")}\n\n请重新授予权限。"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("权限请求")
            .setMessage(message)
            .setPositiveButton("重新授权") { _, _ ->
                checkPermissions()
            }
            .setNegativeButton("稍后再说", null)
            .setCancelable(false)
            .show()
    }
    
    private fun showPermissionPermanentlyDeniedDialog(permanentlyDeniedPermissions: List<String>) {
        val permissionNames = permanentlyDeniedPermissions.map { PermissionUtils.getPermissionName(it) }
        val message = "以下权限被永久拒绝：\n${permissionNames.joinToString("\n")}\n\n请在设置中手动开启这些权限。"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("权限设置")
            .setMessage(message)
            .setPositiveButton("前往设置") { _, _ ->
                PermissionUtils.openAppSettings(this)
            }
            .setNegativeButton("稍后再说", null)
            .setCancelable(false)
            .show()
    }
}
