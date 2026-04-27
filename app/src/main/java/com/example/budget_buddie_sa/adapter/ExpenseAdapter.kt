package com.example.budget_buddie_sa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.data.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(private var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
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
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
