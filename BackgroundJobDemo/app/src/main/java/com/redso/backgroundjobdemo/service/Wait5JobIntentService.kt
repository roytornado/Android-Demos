package com.redso.backgroundjobdemo.service

import android.content.Context
import android.content.Intent
import com.redso.backgroundjobdemo.App
import com.redso.backgroundjobdemo.Common
import java.util.*

class Wait5JobIntentService : BaseJobIntentService() {

  companion object {
    fun enqueueWork(context: Context, intent: Intent) {
      enqueueWork(context, Wait5JobIntentService::class.java, 4000, intent)
      App.shared.testData.add("$this")
    }
  }

  override fun onHandleWork(intent: Intent) {
    super.onHandleWork(intent)
    val time = Date().time
    while (Date().time - time < 1000 * 5) {
    }
    val testData = "${App.shared.testData.size}"
    Common.showNotification(baseContext, "$className finish [$testData]")
  }
}