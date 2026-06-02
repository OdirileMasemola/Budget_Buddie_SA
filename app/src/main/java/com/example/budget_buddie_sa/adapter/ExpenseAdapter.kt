package com.example.budget_buddie_sa.adapter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.budget_buddie_sa.ImagePreviewActivity
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.data.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(private var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val ivIcon: ImageView = view.findViewById(R.id.ivExpenseIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_dummy, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvDescription.text = expense.description
        holder.tvAmount.text = "R ${String.format("%.2f", expense.amount)}"
        
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(expense.date))

        // Requirement 3: Display receipt image if available
        if (!expense.receiptImage.isNullOrEmpty()) {
            try {
                Glide.with(holder.itemView.context)
                    .load(Uri.parse(expense.receiptImage))
                    .centerCrop()
                    .override(300, 300)
                    .placeholder(R.drawable.ic_list)
                    .error(R.drawable.ic_list)
                    .into(holder.ivIcon)

                holder.ivIcon.imageTintList = null // Remove purple tint for real images
                
                // Requirement 4: Click to expand
                holder.ivIcon.setOnClickListener {
                    val intent = Intent(holder.itemView.context, ImagePreviewActivity::class.java)
                    intent.putExtra("image_uri", expense.receiptImage)
                    holder.itemView.context.startActivity(intent)
                }
            } catch (e: Exception) {
                holder.ivIcon.setImageResource(R.drawable.ic_list)
                holder.ivIcon.setOnClickListener(null)
            }
        } else {
            // Default icon
            holder.ivIcon.setImageResource(R.drawable.ic_list)
            holder.ivIcon.setColorFilter(Color.parseColor("#7C3AED"), android.graphics.PorterDuff.Mode.SRC_IN)
            holder.ivIcon.setOnClickListener(null)
        }
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
