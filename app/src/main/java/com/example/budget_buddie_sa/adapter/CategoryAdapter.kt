package com.example.budget_buddie_sa.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.data.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColor: View = view.findViewById(R.id.viewCategoryColor)
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name
        
        try {
            holder.viewColor.background.setColorFilter(Color.parseColor(category.color), PorterDuff.Mode.SRC_IN)
        } catch (e: Exception) {
            holder.viewColor.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        }

        holder.ivDelete.setOnClickListener { onDeleteClick(category) }
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}