package com.redso.backgroundjobdemo.service

import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import com.redso.backgroundjobdemo.Constants

open class BaseJobIntentService : JobIntentService() {

  val className: String get() = this.javaClass.simpleName

  override fun onCreate() {
    super.onCreate()
    Log.i(Constants.TAG, "$className onCreate")
  }

  override fun onHandleWork(intent: Intent) {
    Log.i(Constants.TAG, "$className onHandleWork")
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(Constants.TAG, "$className onDestroy")
  }
}