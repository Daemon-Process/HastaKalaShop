package com.hastakala.shop.ui.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hastakala.shop.R
import com.hastakala.shop.databinding.ActivityBillingBinding
import com.hastakala.shop.model.Product
import com.hastakala.shop.utils.ShopViewModel

class BillingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBillingBinding
    private lateinit var viewModel: ShopViewModel
    private val adapter = ProductGridAdapter { product -> confirmSale(product) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Quick Bill 🛒"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]

        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter

        viewModel.allProducts.observe(this) { products ->
            adapter.submitList(products)
        }

        viewModel.saleSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "✅ Sale Recorded!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmSale(product: Product) {
        if (product.stockCount <= 0) {
            Toast.makeText(this, "❌ Out of stock!", Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Sale")
            .setMessage("${product.emoji} ${product.color} ${product.name}\nPrice: ₹${product.price}\nStock left: ${product.stockCount}")
            .setPositiveButton("Record Sale") { _, _ ->
                viewModel.recordSale(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class ProductGridAdapter(
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductGridAdapter.VH>() {

    private var products = listOf<Product>()

    fun submitList(list: List<Product>) {
        products = list
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.cardProduct)
        val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvColor: TextView = itemView.findViewById(R.id.tvProductColor)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvStock: TextView = itemView.findViewById(R.id.tvStockCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = products[position]
        holder.tvEmoji.text = product.emoji
        holder.tvName.text = product.name
        holder.tvColor.text = product.color
        holder.tvPrice.text = "₹${product.price.toInt()}"
        holder.tvStock.text = "Stock: ${product.stockCount}"

        // Highlight low stock
        val stockColor = if (product.stockCount <= 3)
            holder.itemView.context.getColor(R.color.error)
        else
            holder.itemView.context.getColor(R.color.text_secondary)
        holder.tvStock.setTextColor(stockColor)

        // Grey out if out of stock
        holder.card.alpha = if (product.stockCount <= 0) 0.5f else 1f

        holder.card.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount() = products.size
}
