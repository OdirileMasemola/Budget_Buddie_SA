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
     * @param userId The Firebase UID of the logged-in user (always String).
     */
    fun saveSession(userId: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Retrieves the stored user ID safely.
     * If there's a type mismatch (e.g., old Int value), it clears the session to prevent crashes.
     * @return Firebase UID (String) or null if not found or invalid.
     */
    fun getUserId(): String? {
        return try {
            prefs.getString(KEY_USER_ID, null)
        } catch (e: Exception) {
            // This catches ClassCastException if the old userId was an Integer.
            // Requirement 3 & 5: Clear old data to prevent future crashes.
            clearSession()
            null
        }
    }

    /**
     * Checks if a user is currently logged in.
     * Verifies both the flag and the existence of a valid String userId.
     */
    fun isLoggedIn(): Boolean {
        val hasLoggedInFlag = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        return hasLoggedInFlag && !getUserId().isNullOrEmpty()
    }

    /**
     * Clears all session data (Logout).
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
