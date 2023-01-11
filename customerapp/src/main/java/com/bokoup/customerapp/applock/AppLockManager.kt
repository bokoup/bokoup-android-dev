package com.bokoup.customerapp.applock

interface AppLockManager {

  fun updateCode(code: String)

  fun attemptUnlock(candidate: String): Boolean
}