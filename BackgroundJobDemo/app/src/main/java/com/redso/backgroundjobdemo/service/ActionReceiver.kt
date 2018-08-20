package com.redso.backgroundjobdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.redso.backgroundjobdemo.Common
import com.redso.backgroundjobdemo.Constants
import java.lang.Exception

class ActionReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.let {
      val action = it.action

      Log.i(Constants.TAG, "ActionReceiver onReceive $action")
      if (action == "testBackgroundServiceLimitation") {
        try {
          context?.startService(Intent(context, Wait10IntentService::class.java))
        } catch (e: Exception) {
          context?.let { Common.showNotification(it, "Wait10Receiver failed") }
        }
      }

      if (action == "testJobIntentService") {
        Wait5JobIntentService.enqueueWork(context!!, intent!!)
      }

      if (action == "testNetworkCallJobIntentService") {
        NetworkCallJobIntentService.enqueueWork(context!!, intent!!)
      }

      if (action == "testStepCounterJobIntentService") {
        StepCounterJobIntentService.enqueueWork(context!!, it)
      }

      if (action == "testLocationJobIntentService") {
        LocationJobIntentService.enqueueWork(context!!, it)
      }
    }
  }

}