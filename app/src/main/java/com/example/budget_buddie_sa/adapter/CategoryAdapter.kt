package com.example.budget_buddie_sa.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
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
    private val onItemClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColor: View = view.findViewById(R.id.viewCategoryColor)
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
        val ivIcon: ImageView = view.findViewById(R.id.ivCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name
        
        if (!category.imageUri.isNullOrEmpty()) {
            // Display Image from URI
            holder.ivIcon.visibility = View.VISIBLE
            holder.ivIcon.setImageURI(Uri.parse(category.imageUri))
            // Clear color filter if any
            holder.viewColor.background.clearColorFilter()
        } else {
            // Display Color and Icon
            holder.ivIcon.visibility = View.VISIBLE
            holder.ivIcon.setImageResource(R.drawable.ic_category)
            try {
                val color = Color.parseColor(category.color)
                holder.viewColor.background.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            } catch (e: Exception) {
                holder.viewColor.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
            }
        }

        holder.itemView.setOnClickListener { onItemClick(category) }
        holder.ivDelete.setOnClickListener { onDeleteClick(category) }
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
