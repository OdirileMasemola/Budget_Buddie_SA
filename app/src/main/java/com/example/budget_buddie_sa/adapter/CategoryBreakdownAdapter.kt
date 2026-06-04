package com.example.budget_buddie_sa.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.data.model.Category
import java.text.NumberFormat
import java.util.*

class CategoryBreakdownAdapter(private var items: List<Pair<Category, Double>>) :
    RecyclerView.Adapter<CategoryBreakdownAdapter.ViewHolder>() {

    private var totalSpent: Double = 0.0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorView: View = view.findViewById(R.id.viewCategoryColor)
        val nameTv: TextView = view.findViewById(R.id.tvCategoryName)
        val amountTv: TextView = view.findViewById(R.id.tvCategoryAmount)
        val percentTv: TextView = view.findViewById(R.id.tvCategoryPercent)
        val progressBar: ProgressBar = view.findViewById(R.id.pbCategoryBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_breakdown, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (category, amount) = items[position]
        
        holder.nameTv.text = category.name
        
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        holder.amountTv.text = currencyFormat.format(amount)
        
        val percent = if (totalSpent > 0) ((amount / totalSpent) * 100).toInt() else 0
        holder.percentTv.text = "$percent%"
        holder.progressBar.progress = percent
        
        try {
            val color = Color.parseColor(category.color)
            holder.colorView.background.setTint(color)
            holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(color)
        } catch (e: Exception) {
            val defaultColor = Color.parseColor("#7C3AED")
            holder.colorView.background.setTint(defaultColor)
            holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(defaultColor)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Pair<Category, Double>>) {
        items = newItems.sortedByDescending { it.second }
        totalSpent = items.sumOf { it.second }
        notifyDataSetChanged()
    }
}