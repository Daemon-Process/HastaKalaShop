package com.hastakala.shop.utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hastakala.shop.data.db.AppDatabase
import com.hastakala.shop.data.db.SaleAggregation
import com.hastakala.shop.data.repository.ShopRepository
import com.hastakala.shop.model.Product
import com.hastakala.shop.model.Sale
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: ShopRepository
    val allProducts: LiveData<List<Product>>
    val lowStockProducts: LiveData<List<Product>>
    val allSales: LiveData<List<Sale>>
    val salesByProduct: LiveData<List<SaleAggregation>>
    val salesByCategory: LiveData<List<SaleAggregation>>
    val totalRevenue: LiveData<Double>

    private val _weekRevenue = MutableLiveData<Double>()
    val weekRevenue: LiveData<Double> = _weekRevenue

    private val _monthRevenue = MutableLiveData<Double>()
    val monthRevenue: LiveData<Double> = _monthRevenue

    private val _saleSuccess = MutableLiveData<Boolean>()
    val saleSuccess: LiveData<Boolean> = _saleSuccess

    init {
        val db = AppDatabase.getDatabase(application)
        repo = ShopRepository(db)
        allProducts = repo.allProducts
        lowStockProducts = repo.lowStockProducts
        allSales = repo.allSales
        salesByProduct = repo.salesByProduct
        salesByCategory = repo.salesByCategory
        totalRevenue = repo.totalRevenue
        loadRevenues()
    }

    fun loadRevenues() {
        viewModelScope.launch {
            _weekRevenue.postValue(repo.getWeekRevenue())
            _monthRevenue.postValue(repo.getMonthRevenue())
        }
    }

    fun recordSale(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            val sale = Sale(
                productId = product.id,
                productName = product.name,
                category = product.category,
                color = product.color,
                emoji = product.emoji,
                price = product.price,
                quantity = quantity
            )
            repo.recordSale(sale)
            _saleSuccess.postValue(true)
            loadRevenues()
        }
    }

    fun getSalesThisWeek() = repo.getSalesThisWeek()
    fun getSalesThisMonth() = repo.getSalesThisMonth()

    fun insertProduct(product: Product) {
        viewModelScope.launch { repo.insertProduct(product) }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { repo.updateProduct(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repo.deleteProduct(product) }
    }
}
