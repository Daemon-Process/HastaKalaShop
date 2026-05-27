package com.hastakala.shop.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hastakala.shop.model.Product
import com.hastakala.shop.model.Sale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, Sale::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hastakala_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.productDao())
                }
            }
        }

        suspend fun populateDatabase(productDao: ProductDao) {
            val products = listOf(
                // Banana Fiber Bags
                Product(name = "Banana Fiber Bag", category = "Bag", color = "Brown", emoji = "👜", stockCount = 15, price = 350.0),
                Product(name = "Banana Fiber Bag", category = "Bag", color = "Blue", emoji = "👜", stockCount = 12, price = 350.0),
                Product(name = "Banana Fiber Bag", category = "Bag", color = "Red", emoji = "👜", stockCount = 8, price = 350.0),
                Product(name = "Banana Fiber Bag", category = "Bag", color = "Green", emoji = "👜", stockCount = 10, price = 350.0),
                // Keychains
                Product(name = "Keychain", category = "Keychain", color = "Yellow", emoji = "🔑", stockCount = 25, price = 80.0),
                Product(name = "Keychain", category = "Keychain", color = "Pink", emoji = "🔑", stockCount = 20, price = 80.0),
                Product(name = "Keychain", category = "Keychain", color = "Purple", emoji = "🔑", stockCount = 18, price = 80.0),
                Product(name = "Keychain", category = "Keychain", color = "Orange", emoji = "🔑", stockCount = 22, price = 80.0),
                // Baskets
                Product(name = "Woven Basket", category = "Basket", color = "Natural", emoji = "🧺", stockCount = 10, price = 500.0),
                Product(name = "Woven Basket", category = "Basket", color = "Maroon", emoji = "🧺", stockCount = 7, price = 550.0),
                // Earrings
                Product(name = "Craft Earrings", category = "Jewellery", color = "Gold", emoji = "💛", stockCount = 30, price = 120.0),
                Product(name = "Craft Earrings", category = "Jewellery", color = "Silver", emoji = "🩶", stockCount = 28, price = 120.0),
                // Pouches
                Product(name = "Fiber Pouch", category = "Pouch", color = "Beige", emoji = "👝", stockCount = 14, price = 200.0),
                Product(name = "Fiber Pouch", category = "Pouch", color = "Blue", emoji = "👝", stockCount = 11, price = 200.0),
                // Coasters
                Product(name = "Coaster Set", category = "Coaster", color = "Natural", emoji = "🪵", stockCount = 20, price = 150.0),
                Product(name = "Coaster Set", category = "Coaster", color = "Dark Brown", emoji = "🪵", stockCount = 16, price = 150.0),
            )
            productDao.insertAll(products)
        }
    }
}
