package com.productmanager.app.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.productmanager.app.ProductManagerApplication
import com.productmanager.app.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment() {
    
    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CategoryListViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeData()
    }
    
    private fun setupViewModel() {
        val application = requireActivity().application as ProductManagerApplication
        val factory = CategoryListViewModelFactory(application.productRepository)
        viewModel = ViewModelProvider(this, factory)[CategoryListViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onCategoryClick = { category ->
                // TODO: 导航到分类商品列表
                Toast.makeText(requireContext(), "查看分类：${category.name}", Toast.LENGTH_SHORT).show()
            },
            onCategoryEdit = { category ->
                // TODO: 编辑分类
                Toast.makeText(requireContext(), "编辑分类：${category.name}", Toast.LENGTH_SHORT).show()
            },
            onCategoryDelete = { category ->
                showDeleteConfirmDialog(category.id, category.name)
            }
        )
        
        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }
    
    private fun observeData() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
            
            if (categories.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewCategories.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewCategories.visibility = View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun showAddCategoryDialog() {
        val input = android.widget.EditText(requireContext())
        input.hint = "分类名称"
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("添加分类")
            .setView(input)
            .setPositiveButton("添加") { _, _ ->
                val categoryName = input.text.toString().trim()
                if (categoryName.isNotEmpty()) {
                    viewModel.addCategory(categoryName)
                    Toast.makeText(requireContext(), "分类已添加", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showDeleteConfirmDialog(categoryId: Long, categoryName: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("删除分类")
            .setMessage("确定要删除分类「$categoryName」吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteCategory(categoryId)
                Toast.makeText(requireContext(), "分类已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
