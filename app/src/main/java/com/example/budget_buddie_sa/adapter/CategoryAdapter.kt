package com.example.budget_buddie_sa.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.budget_buddie_sa.ImagePreviewActivity
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
        
        Log.d("CategoryAdapter", "Binding category: ${category.name}, imageUrl: ${category.imageUrl}")

        // Requirement 6 & 7: Category image display logic
        if (!category.imageUrl.isNullOrEmpty()) {
            Log.d("CategoryAdapter", "Loading imageUrl for ${category.name}: ${category.imageUrl}")
            holder.ivIcon.visibility = View.VISIBLE
            // Reset tint before loading image
            holder.ivIcon.imageTintList = null
            holder.ivIcon.colorFilter = null

            Glide.with(holder.itemView.context)
                .load(category.imageUrl)
                .centerCrop()
                .override(300, 300)
                .into(holder.ivIcon)

            // Requirement 4: Click to expand (using ImagePreviewActivity)
            holder.ivIcon.setOnClickListener {
                Log.d("CategoryAdapter", "Opening ImagePreviewActivity with imageUrl: ${category.imageUrl}")
                val intent = Intent(holder.itemView.context, ImagePreviewActivity::class.java)
                intent.putExtra("imageUrl", category.imageUrl)
                holder.itemView.context.startActivity(intent)
            }
        } else {
            // No image, use color block or default icon
            holder.ivIcon.visibility = View.VISIBLE
            holder.ivIcon.setImageResource(R.drawable.ic_category)
            holder.ivIcon.setColorFilter(Color.parseColor("#7C3AED"), PorterDuff.Mode.SRC_IN)
            holder.ivIcon.setOnClickListener(null)

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
