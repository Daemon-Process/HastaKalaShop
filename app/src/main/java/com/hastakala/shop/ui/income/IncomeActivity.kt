package com.hastakala.shop.ui.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.hastakala.shop.R
import com.hastakala.shop.databinding.ActivityIncomeBinding
import com.hastakala.shop.model.Sale
import com.hastakala.shop.utils.ShopViewModel
import java.text.SimpleDateFormat
import java.util.*

class IncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomeBinding
    private lateinit var viewModel: ShopViewModel
    private val adapter = SaleLogAdapter()
    private var showingWeek = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Income Log 💰"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]

        binding.rvSales.layoutManager = LinearLayoutManager(this)
        binding.rvSales.adapter = adapter

        setupBarChart()
        setupToggle()
        loadWeekData()

        viewModel.totalRevenue.observe(this) { revenue ->
            binding.tvTotalAllTime.text = "All-time: ₹${String.format("%.2f", revenue ?: 0.0)}"
        }
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            axisLeft.textColor = resources.getColor(R.color.text_primary, null)
            axisRight.isEnabled = false
            xAxis.textColor = resources.getColor(R.color.text_primary, null)
            xAxis.granularity = 1f
            animateY(800)
        }
    }

    private fun setupToggle() {
        binding.btnThisWeek.setOnClickListener {
            showingWeek = true
            binding.btnThisWeek.isSelected = true
            binding.btnThisMonth.isSelected = false
            loadWeekData()
        }
        binding.btnThisMonth.setOnClickListener {
            showingWeek = false
            binding.btnThisMonth.isSelected = true
            binding.btnThisWeek.isSelected = false
            loadMonthData()
        }
        binding.btnThisWeek.isSelected = true
    }

    private fun loadWeekData() {
        viewModel.getSalesThisWeek().observe(this) { sales ->
            updateUI(sales, "This Week")
        }
        viewModel.weekRevenue.observe(this) { rev ->
            binding.tvPeriodRevenue.text = "This Week: ₹${String.format("%.2f", rev ?: 0.0)}"
        }
    }

    private fun loadMonthData() {
        viewModel.getSalesThisMonth().observe(this) { sales ->
            updateUI(sales, "This Month")
        }
        viewModel.monthRevenue.observe(this) { rev ->
            binding.tvPeriodRevenue.text = "This Month: ₹${String.format("%.2f", rev ?: 0.0)}"
        }
    }

    private fun updateUI(sales: List<Sale>, period: String) {
        adapter.submitList(sales)
        binding.tvSaleCount.text = "${sales.size} sales $period"

        if (sales.isEmpty()) {
            binding.tvNoSales.visibility = View.VISIBLE
            binding.barChart.visibility = View.GONE
        } else {
            binding.tvNoSales.visibility = View.GONE
            binding.barChart.visibility = View.VISIBLE
            updateBarChart(sales)
        }
    }

    private fun updateBarChart(sales: List<Sale>) {
        val grouped = sales.groupBy { it.category }
        val entries = grouped.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.sumOf { it.price * it.quantity }.toFloat())
        }
        val labels = grouped.keys.toList()

        val dataSet = BarDataSet(entries, "Revenue by Category").apply {
            colors = listOf(
                resources.getColor(R.color.chart1, null),
                resources.getColor(R.color.chart2, null),
                resources.getColor(R.color.chart3, null),
                resources.getColor(R.color.chart4, null),
                resources.getColor(R.color.chart5, null),
            )
            valueTextColor = resources.getColor(R.color.text_primary, null)
            valueTextSize = 10f
        }

        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.data = BarData(dataSet)
        binding.barChart.invalidate()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class SaleLogAdapter : RecyclerView.Adapter<SaleLogAdapter.VH>() {

    private var sales = listOf<Sale>()
    private val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    fun submitList(list: List<Sale>) {
        sales = list
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmoji: TextView = itemView.findViewById(R.id.tvSaleEmoji)
        val tvName: TextView = itemView.findViewById(R.id.tvSaleName)
        val tvTime: TextView = itemView.findViewById(R.id.tvSaleTime)
        val tvAmount: TextView = itemView.findViewById(R.id.tvSaleAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sale_row, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val sale = sales[position]
        holder.tvEmoji.text = sale.emoji
        holder.tvName.text = "${sale.color} ${sale.productName}"
        holder.tvTime.text = sdf.format(Date(sale.timestamp))
        holder.tvAmount.text = "+ ₹${String.format("%.0f", sale.price * sale.quantity)}"
    }

    override fun getItemCount() = sales.size
}
