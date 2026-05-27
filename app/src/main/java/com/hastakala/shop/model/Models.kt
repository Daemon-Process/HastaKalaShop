package com.hastakala.shop.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a type of product with color/variant
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,   // e.g., "Bag", "Keychain", "Basket"
    val color: String,
    val emoji: String,      // e.g., "👜", "🔑", "🧺"
    val stockCount: Int = 10,
    val price: Double = 0.0
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productName: String,
    val category: String,
    val color: String,
    val emoji: String,
    val price: Double,
    val quantity: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

data class SalesSummary(
    val label: String,
    val count: Int,
    val totalRevenue: Double
)

data class ProductWithSales(
    val product: Product,
    val totalSold: Int,
    val revenue: Double
)
