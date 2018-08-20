package com.redso.backgroundjobdemo.service

import android.content.Context
import android.content.Intent
import com.redso.backgroundjobdemo.Common
import java.util.*

class NetworkCallJobIntentService : BaseJobIntentService() {
  companion object {
    fun enqueueWork(context: Context, intent: Intent) {
      enqueueWork(context, NetworkCallJobIntentService::class.java, 4001, intent)
    }
  }

  override fun onHandleWork(intent: Intent) {
    super.onHandleWork(intent)
    mockNetworkCall(3) {
      Common.showNotification(baseContext, "NetworkCallJobIntentService finish 3")
    }
    mockNetworkCall(5) {
      Common.showNotification(baseContext, "NetworkCallJobIntentService finish 5")
    }

  }

  fun mockNetworkCall(sec: Int, completion: () -> Unit) {
    Runnable {
      val time = Date().time
      while (Date().time - time < 1000 * sec) {
      }
      completion()
    }.run()
  }
}