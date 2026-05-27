package com.hastakala.shop.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hastakala.shop.R
import com.hastakala.shop.databinding.ActivityStockBinding
import com.hastakala.shop.model.Product
import com.hastakala.shop.utils.ShopViewModel

class StockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStockBinding
    private lateinit var viewModel: ShopViewModel
    private val adapter = StockAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Stock Overview 📦"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]

        binding.rvStock.layoutManager = LinearLayoutManager(this)
        binding.rvStock.adapter = adapter

        viewModel.allProducts.observe(this) { products ->
            adapter.submitList(products.sortedBy { it.stockCount })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class StockAdapter : RecyclerView.Adapter<StockAdapter.VH>() {

    private var items = listOf<Product>()

    fun submitList(list: List<Product>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmoji: TextView = itemView.findViewById(R.id.tvStockEmoji)
        val tvName: TextView = itemView.findViewById(R.id.tvStockName)
        val tvColor: TextView = itemView.findViewById(R.id.tvStockColor)
        val tvCount: TextView = itemView.findViewById(R.id.tvStockCount)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStockStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock_row, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = items[position]
        holder.tvEmoji.text = product.emoji
        holder.tvName.text = product.name
        holder.tvColor.text = product.color
        holder.tvCount.text = "${product.stockCount} units"

        when {
            product.stockCount == 0 -> {
                holder.tvStatus.text = "OUT OF STOCK"
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.error))
            }
            product.stockCount <= 3 -> {
                holder.tvStatus.text = "⚠️ LOW"
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.warning))
            }
            else -> {
                holder.tvStatus.text = "✅ OK"
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.success))
            }
        }
    }

    override fun getItemCount() = items.size
}
