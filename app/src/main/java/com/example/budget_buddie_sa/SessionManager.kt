package com.example.budget_buddie_sa

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user session state using SharedPreferences.
 * Updated to support Firebase UID (String).
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Saves user session info.
     * @param userId The Firebase UID of the logged-in user.
     */
    fun saveSession(userId: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Retrieves the stored user ID.
     * @return Firebase UID or null if not found.
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Clears all session data (Logout).
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
