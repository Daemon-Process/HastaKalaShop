package com.hastakala.shop.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("HastaKalaSession", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USERNAME = "username"
        const val KEY_SHOP_NAME = "shop_name"
    }

    fun saveLogin(username: String, shopName: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USERNAME, username)
            putString(KEY_SHOP_NAME, shopName)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "Artisan") ?: "Artisan"

    fun getShopName(): String = prefs.getString(KEY_SHOP_NAME, "My Shop") ?: "My Shop"

    fun logout() {
        prefs.edit().clear().apply()
    }
}
