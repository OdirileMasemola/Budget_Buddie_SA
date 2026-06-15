package com.example.budget_buddie_sa.data.repository

import android.util.Log
import com.example.budget_buddie_sa.data.model.Badge
import com.example.budget_buddie_sa.data.model.Budget
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import com.example.budget_buddie_sa.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for syncing local Room data with Firebase Firestore.
 * Implements offline-first logic by handling sync operations.
 */
class FirebaseSyncRepository {

    private val db = FirebaseFirestore.getInstance()

    // --- User Sync ---

    suspend fun syncUser(user: User) {
        try {
            db.collection("users")
                .document(user.id)
                .set(user)
                .await()
            Log.d("FirebaseSync", "User synced: ${user.id}")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error syncing user: ${e.message}")
        }
    }

    suspend fun fetchUser(userId: String): User? {
        return try {
            db.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error fetching user: ${e.message}")
            null
        }
    }

    // --- Category Sync ---

    suspend fun syncCategory(category: Category) {
        try {
            db.collection("users")
                .document(category.userId)
                .collection("categories")
                .document(category.id)
                .set(category)
                .await()
            Log.d("FirebaseSync", "Category synced: ${category.id}")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error syncing category: ${e.message}")
        }
    }

    suspend fun deleteCategory(userId: String, categoryId: String) {
        try {
            db.collection("users")
                .document(userId)
                .collection("categories")
                .document(categoryId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error deleting category: ${e.message}")
        }
    }

    // --- Expense Sync ---

    suspend fun syncExpense(expense: Expense) {
        try {
            db.collection("users")
                .document(expense.userId)
                .collection("expenses")
                .document(expense.id)
                .set(expense)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error syncing expense: ${e.message}")
        }
    }

    suspend fun deleteExpense(userId: String, expenseId: String) {
        try {
            db.collection("users")
                .document(userId)
                .collection("expenses")
                .document(expenseId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error deleting expense: ${e.message}")
        }
    }

    // --- Budget Sync ---

    suspend fun syncBudget(budget: Budget) {
        try {
            db.collection("users")
                .document(budget.userId)
                .collection("budgets")
                .document(budget.id)
                .set(budget)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error syncing budget: ${e.message}")
        }
    }

    // --- Badge Sync ---

    suspend fun syncBadge(badge: Badge) {
        try {
            db.collection("users")
                .document(badge.userId)
                .collection("badges")
                .document(badge.badgeId)
                .set(badge)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error syncing badge: ${e.message}")
        }
    }

    data class UserDataSnapshot(
        val user: User?,
        val categories: List<Category>,
        val expenses: List<Expense>,
        val budgets: List<Budget>,
        val badges: List<Badge>
    )

    /**
     * Fetches all data from Firestore for a user to populate Room on login.
     */
    suspend fun fetchAllUserData(userId: String): UserDataSnapshot {
        val user = fetchUser(userId)

        val categories = db.collection("users").document(userId).collection("categories")
            .get().await().toObjects(Category::class.java)
        
        val expenses = db.collection("users").document(userId).collection("expenses")
            .get().await().toObjects(Expense::class.java)
            
        val budgets = db.collection("users").document(userId).collection("budgets")
            .get().await().toObjects(Budget::class.java)

        val badges = db.collection("users").document(userId).collection("badges")
            .get().await().toObjects(Badge::class.java)

        return UserDataSnapshot(user, categories, expenses, budgets, badges)
    }
}
