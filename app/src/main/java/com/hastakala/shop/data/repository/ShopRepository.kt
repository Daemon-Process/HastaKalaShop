package com.hastakala.shop.data.repository

import androidx.lifecycle.LiveData
import com.hastakala.shop.data.db.AppDatabase
import com.hastakala.shop.data.db.SaleAggregation
import com.hastakala.shop.model.Product
import com.hastakala.shop.model.Sale
import java.util.Calendar

class ShopRepository(private val db: AppDatabase) {

    // Products
    val allProducts: LiveData<List<Product>> = db.productDao().getAllProducts()
    val lowStockProducts: LiveData<List<Product>> = db.productDao().getLowStockProducts()

    suspend fun getAllProductsSync(): List<Product> = db.productDao().getAllProductsSync()
    suspend fun insertProduct(product: Product) = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: Product) = db.productDao().updateProduct(product)
    suspend fun deleteProduct(product: Product) = db.productDao().deleteProduct(product)

    // Sales
    val allSales: LiveData<List<Sale>> = db.saleDao().getAllSales()
    val salesByProduct: LiveData<List<SaleAggregation>> = db.saleDao().getSalesByProduct()
    val salesByCategory: LiveData<List<SaleAggregation>> = db.saleDao().getSalesByCategory()
    val totalRevenue: LiveData<Double> = db.saleDao().getTotalRevenue()

    suspend fun recordSale(sale: Sale) {
        db.saleDao().insertSale(sale)
        db.productDao().decrementStock(sale.productId, sale.quantity)
    }

    fun getSalesThisWeek(): LiveData<List<Sale>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return db.saleDao().getSalesThisWeek(cal.timeInMillis)
    }

    fun getSalesThisMonth(): LiveData<List<Sale>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return db.saleDao().getSalesThisMonth(cal.timeInMillis)
    }

    suspend fun getWeekRevenue(): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        return db.saleDao().getWeekRevenue(cal.timeInMillis) ?: 0.0
    }

    suspend fun getMonthRevenue(): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        return db.saleDao().getMonthRevenue(cal.timeInMillis) ?: 0.0
    }
}
