package com.bokoup.customerapp.applock

import android.content.SharedPreferences
import androidx.core.content.edit

private const val PREF_CODE_APP_LOCK_CODE = "app_lock_code"

class AppLockManagerImpl(
  private val sharedPreferences: SharedPreferences,
) : AppLockManager {

  override fun updateCode(code: String) {
    sharedPreferences.edit {
      putString(PREF_CODE_APP_LOCK_CODE, code)
    }
  }

  override fun attemptUnlock(candidate: String): Boolean {
    return candidate == getCode()
  }

  private fun getCode(): String? {
    return sharedPreferences.getString(PREF_CODE_APP_LOCK_CODE, null)
  }
}