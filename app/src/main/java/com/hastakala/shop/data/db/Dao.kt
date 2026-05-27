package com.hastakala.shop.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hastakala.shop.model.Product
import com.hastakala.shop.model.Sale

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProductsSync(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET stockCount = stockCount - :qty WHERE id = :productId AND stockCount >= :qty")
    suspend fun decrementStock(productId: Int, qty: Int)

    @Query("SELECT * FROM products WHERE stockCount <= 3")
    fun getLowStockProducts(): LiveData<List<Product>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int

    @Delete
    suspend fun deleteProduct(product: Product)
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    fun getAllSales(): LiveData<List<Sale>>

    @Insert
    suspend fun insertSale(sale: Sale)

    // Sales this week
    @Query("SELECT * FROM sales WHERE timestamp >= :weekStart ORDER BY timestamp DESC")
    fun getSalesThisWeek(weekStart: Long): LiveData<List<Sale>>

    // Sales this month
    @Query("SELECT * FROM sales WHERE timestamp >= :monthStart ORDER BY timestamp DESC")
    fun getSalesThisMonth(monthStart: Long): LiveData<List<Sale>>

    // Total revenue
    @Query("SELECT SUM(price * quantity) FROM sales")
    fun getTotalRevenue(): LiveData<Double>

    // Revenue this week
    @Query("SELECT SUM(price * quantity) FROM sales WHERE timestamp >= :weekStart")
    suspend fun getWeekRevenue(weekStart: Long): Double

    // Revenue this month
    @Query("SELECT SUM(price * quantity) FROM sales WHERE timestamp >= :monthStart")
    suspend fun getMonthRevenue(monthStart: Long): Double

    // Group sales by product name for pie chart
    @Query("""
        SELECT productName || ' (' || color || ')' as label, 
               SUM(quantity) as totalCount,
               SUM(price * quantity) as totalRevenue
        FROM sales 
        GROUP BY productId 
        ORDER BY totalCount DESC
    """)
    fun getSalesByProduct(): LiveData<List<SaleAggregation>>

    // Group by category
    @Query("""
        SELECT category as label, 
               SUM(quantity) as totalCount,
               SUM(price * quantity) as totalRevenue
        FROM sales 
        GROUP BY category 
        ORDER BY totalCount DESC
    """)
    fun getSalesByCategory(): LiveData<List<SaleAggregation>>
}

data class SaleAggregation(
    val label: String,
    val totalCount: Int,
    val totalRevenue: Double
)
