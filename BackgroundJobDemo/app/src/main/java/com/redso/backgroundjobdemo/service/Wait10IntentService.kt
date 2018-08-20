package com.redso.backgroundjobdemo.service

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.redso.backgroundjobdemo.Common
import com.redso.backgroundjobdemo.Constants
import java.lang.Exception
import java.util.*

class Wait10IntentService : IntentService("Wait10IntentService") {

  override fun onHandleIntent(intent: Intent?) {
    Log.i(Constants.TAG, "Wait10IntentService onHandleIntent")
    val time = Date().time
    while (Date().time - time < 1000 * 10) {
    }
    Log.i(Constants.TAG, "Wait10IntentService finish")
    Common.showNotification(baseContext, "Wait10IntentService finish")
  }
}
