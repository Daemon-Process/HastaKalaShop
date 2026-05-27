package com.hastakala.shop.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hastakala.shop.databinding.ActivityLoginBinding
import com.hastakala.shop.ui.dashboard.MainActivity
import com.hastakala.shop.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)

        // Skip login if already logged in
        if (session.isLoggedIn()) {
            goToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val shopName = binding.etShopName.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                username.isEmpty() -> binding.etUsername.error = "Enter your name"
                shopName.isEmpty() -> binding.etShopName.error = "Enter shop name"
                password.isEmpty() -> binding.etPassword.error = "Enter password"
                password.length < 4 -> binding.etPassword.error = "Min 4 characters"
                else -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    // Simulate brief loading for UX
                    binding.root.postDelayed({
                        session.saveLogin(username, shopName)
                        goToMain()
                    }, 800)
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
