package com.example.budget_buddie_sa.adapter

import android.content.Intent
import android.graphics.Color
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

        Log.d("ExpenseAdapter", "Binding expense: ${expense.description}, imageUrl: ${expense.imageUrl}")

        // Requirement 5, 6 & 7: Display receipt image if available
        if (!expense.imageUrl.isNullOrEmpty()) {
            Log.d("ExpenseAdapter", "Loading imageUrl for ${expense.description}: ${expense.imageUrl}")
            
            // Reset tint before loading image
            holder.ivIcon.imageTintList = null
            holder.ivIcon.colorFilter = null

            Glide.with(holder.itemView.context)
                .load(expense.imageUrl)
                .centerCrop()
                .override(300, 300)
                .into(holder.ivIcon)
            
            // Requirement 4: Click to expand
            holder.ivIcon.setOnClickListener {
                Log.d("ExpenseAdapter", "Opening ImagePreviewActivity with imageUrl: ${expense.imageUrl}")
                val intent = Intent(holder.itemView.context, ImagePreviewActivity::class.java)
                intent.putExtra("imageUrl", expense.imageUrl)
                holder.itemView.context.startActivity(intent)
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
