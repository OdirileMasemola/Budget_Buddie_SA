package com.example.budget_buddie_sa.data.repository

import android.net.Uri
import android.util.Log
import com.example.budget_buddie_sa.data.local.ExpenseDao
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Expenses with Cloud Sync.
 */
class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val syncRepository: FirebaseSyncRepository,
    private val localImageRepository: LocalImageRepository
) {

    fun getExpensesForUser(userId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesForUser(userId)
    }

    suspend fun insertExpense(expense: Expense, receiptUri: Uri? = null) {
        var finalExpense = expense
        
        if (receiptUri != null) {
            val fileName = "expense_${expense.id}.jpg"
            Log.d("ExpenseRepository", "Saving receipt locally from: $receiptUri")
            val localPath = localImageRepository.saveImageToInternalStorage(receiptUri, fileName)
            if (localPath != null) {
                Log.d("ExpenseRepository", "Receipt saved locally. Path: $localPath")
                finalExpense = finalExpense.copy(imageUrl = localPath)
            } else {
                Log.e("ExpenseRepository", "Local receipt save failed.")
            }
        }

        Log.d("ExpenseRepository", "Saving expense to Room & Firestore: ${finalExpense.description}, imageUrl: ${finalExpense.imageUrl}")
        expenseDao.insertExpense(finalExpense)
        syncRepository.syncExpense(finalExpense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
        syncRepository.deleteExpense(expense.userId, expense.id)
    }

    fun getTotalSpendingForUser(userId: String): Flow<Double?> {
        return expenseDao.getTotalSpendingForUser(userId)
    }

    fun getSpendingForPeriod(userId: String, startDate: Long, endDate: Long): Flow<Double?> {
        return expenseDao.getSpendingForPeriod(userId, startDate, endDate)
    }

    /**
     * Used during login sync to update local RoomDB with Firestore data.
     */
    suspend fun syncFromCloud(expenses: List<Expense>) {
        expenses.forEach { expenseDao.insertExpense(it) }
    }
}
