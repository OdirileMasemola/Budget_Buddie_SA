package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.UserDao
import com.example.budget_buddie_sa.data.model.User

/**
 * Repository to handle user data operations using Room and Firestore.
 */
class UserRepository(
    private val userDao: UserDao,
    private val syncRepository: FirebaseSyncRepository
) {
    suspend fun insertUser(user: User) {
        userDao.insert(user)
        syncRepository.syncUser(user)
    }

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    suspend fun syncFromCloud(user: User) {
        userDao.insert(user)
    }
}
