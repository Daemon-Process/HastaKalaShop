package com.hastakala.shop.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.hastakala.shop.R
import com.hastakala.shop.databinding.ActivityMainBinding
import com.hastakala.shop.ui.billing.BillingActivity
import com.hastakala.shop.ui.income.IncomeActivity
import com.hastakala.shop.ui.stock.StockActivity
import com.hastakala.shop.utils.SessionManager
import com.hastakala.shop.utils.ShopViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ShopViewModel
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]

        binding.tvGreeting.text = "Hello, ${session.getUsername()}! 👋"
        binding.tvShopName.text = session.getShopName()

        setupPieChart()
        setupObservers()
        setupNavigation()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 45f
            setHoleColor(resources.getColor(R.color.background, null))
            setDrawCenterText(true)
            centerText = "Best\nSellers"
            setCenterTextSize(14f)
            legend.isEnabled = true
            setEntryLabelTextSize(11f)
            animateY(1200)
        }
    }

    private fun setupObservers() {
        viewModel.salesByProduct.observe(this) { aggregations ->
            if (aggregations.isEmpty()) {
                binding.tvNoPieData.visibility = android.view.View.VISIBLE
                binding.pieChart.visibility = android.view.View.GONE
            } else {
                binding.tvNoPieData.visibility = android.view.View.GONE
                binding.pieChart.visibility = android.view.View.VISIBLE

                val top5 = aggregations.take(6)
                val entries = top5.map { PieEntry(it.totalCount.toFloat(), it.label) }

                val colors = listOf(
                    resources.getColor(R.color.chart1, null),
                    resources.getColor(R.color.chart2, null),
                    resources.getColor(R.color.chart3, null),
                    resources.getColor(R.color.chart4, null),
                    resources.getColor(R.color.chart5, null),
                    resources.getColor(R.color.chart6, null),
                )

                val dataSet = PieDataSet(entries, "").apply {
                    this.colors = colors
                    sliceSpace = 3f
                    selectionShift = 5f
                    valueTextSize = 10f
                }

                val data = PieData(dataSet).apply {
                    setValueFormatter(PercentFormatter(binding.pieChart))
                    setValueTextSize(10f)
                }
                binding.pieChart.data = data
                binding.pieChart.invalidate()
            }
        }

        viewModel.totalRevenue.observe(this) { revenue ->
            binding.tvTotalRevenue.text = "₹ ${String.format("%.2f", revenue ?: 0.0)}"
        }

        viewModel.weekRevenue.observe(this) { revenue ->
            binding.tvWeekRevenue.text = "₹ ${String.format("%.2f", revenue ?: 0.0)}"
        }

        viewModel.lowStockProducts.observe(this) { products ->
            if (products.isNotEmpty()) {
                binding.cardLowStock.visibility = android.view.View.VISIBLE
                val alert = products.take(3).joinToString("\n") {
                    "⚠️ Only ${it.stockCount} ${it.color} ${it.name} left!"
                }
                binding.tvLowStockAlert.text = alert
            } else {
                binding.cardLowStock.visibility = android.view.View.GONE
            }
        }
    }

    private fun setupNavigation() {
        binding.cardQuickBill.setOnClickListener {
            startActivity(Intent(this, BillingActivity::class.java))
        }
        binding.cardStock.setOnClickListener {
            startActivity(Intent(this, StockActivity::class.java))
        }
        binding.cardIncome.setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }
        binding.fabNewSale.setOnClickListener {
            startActivity(Intent(this, BillingActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRevenues()
    }
}
