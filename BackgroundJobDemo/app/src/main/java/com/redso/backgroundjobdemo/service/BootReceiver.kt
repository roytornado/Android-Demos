package com.redso.backgroundjobdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.redso.backgroundjobdemo.App

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    StepCounterJobIntentService.resetData()
    App.shared.testRepeatAction("testStepCounterJobIntentService", 5, 60 * 5)
  }

}