package com.productmanager.app.ui.product

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.productmanager.app.ProductManagerApplication
import com.productmanager.app.R
import com.productmanager.app.databinding.FragmentProductListBinding
import com.productmanager.app.ui.product.adapter.ProductListAdapter

class ProductListFragment : Fragment() {
    
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ProductListViewModel
    private lateinit var productAdapter: ProductListAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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
        val factory = ProductListViewModelFactory(application.productRepository)
        viewModel = ViewModelProvider(this, factory)[ProductListViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductListAdapter(
            onProductClick = { product ->
                val action = ProductListFragmentDirections
                    .actionProductsToProductDetail(product.id)
                findNavController().navigate(action)
            },
            onProductLongClick = { product ->
                showProductOptionsDialog(product.id)
            }
        )
        
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_products_to_add_product)
        }
        
        binding.chipAll.setOnClickListener {
            viewModel.filterByStatus(null)
            updateChipSelection(binding.chipAll.id)
        }
        
        binding.chipActive.setOnClickListener {
            viewModel.filterByStatus(com.productmanager.app.data.entity.ProductStatus.ACTIVE)
            updateChipSelection(binding.chipActive.id)
        }
        
        binding.chipInactive.setOnClickListener {
            viewModel.filterByStatus(com.productmanager.app.data.entity.ProductStatus.INACTIVE)
            updateChipSelection(binding.chipInactive.id)
        }
        
        binding.chipDraft.setOnClickListener {
            viewModel.filterByStatus(com.productmanager.app.data.entity.ProductStatus.DRAFT)
            updateChipSelection(binding.chipDraft.id)
        }
    }
    
    private fun observeData() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            
            // 更新UI状态
            if (products.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewProducts.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewProducts.visibility = View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
    }
    
    private fun updateChipSelection(selectedChipId: Int) {
        binding.chipAll.isChecked = selectedChipId == binding.chipAll.id
        binding.chipActive.isChecked = selectedChipId == binding.chipActive.id
        binding.chipInactive.isChecked = selectedChipId == binding.chipInactive.id
        binding.chipDraft.isChecked = selectedChipId == binding.chipDraft.id
    }
    
    private fun showProductOptionsDialog(productId: Long) {
        val options = arrayOf("查看详情", "编辑", "复制", "删除")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("商品操作")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // 查看详情
                        val action = ProductListFragmentDirections
                            .actionProductsToProductDetail(productId)
                        findNavController().navigate(action)
                    }
                    1 -> {
                        // 编辑
                        // TODO: 导航到编辑页面
                        Toast.makeText(requireContext(), "编辑功能开发中", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        // 复制
                        viewModel.duplicateProduct(productId)
                        Toast.makeText(requireContext(), "商品已复制", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        // 删除
                        showDeleteConfirmDialog(productId)
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showDeleteConfirmDialog(productId: Long) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("删除商品")
            .setMessage("确定要删除这个商品吗？此操作不可撤销。")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteProduct(productId)
                Toast.makeText(requireContext(), "商品已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_list_menu, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchProducts(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.clearSearch()
                } else {
                    viewModel.searchProducts(newText)
                }
                return true
            }
        })
        
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showSortDialog() {
        val sortOptions = arrayOf("按更新时间", "按创建时间", "按标题", "按价格", "按库存")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("排序方式")
            .setItems(sortOptions) { _, which ->
                val sortBy = when (which) {
                    0 -> "updated"
                    1 -> "created"
                    2 -> "title"
                    3 -> "price"
                    4 -> "stock"
                    else -> "updated"
                }
                viewModel.sortProducts(sortBy)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showFilterDialog() {
        // TODO: 实现更复杂的筛选对话框
        Toast.makeText(requireContext(), "高级筛选功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
