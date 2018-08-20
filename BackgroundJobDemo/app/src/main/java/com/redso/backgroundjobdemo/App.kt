package com.redso.backgroundjobdemo

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import com.redso.backgroundjobdemo.service.ActionReceiver

class App : Application() {
  val className: String get() = this.javaClass.simpleName

  private object Holder {
    var INSTANCE: App? = null
  }

  companion object {
    val shared: App get() = Holder.INSTANCE!!
    val context: Context get() = shared.applicationContext
  }

  var testData = mutableListOf<String>()

  override fun onCreate() {
    super.onCreate()
    Log.i(Constants.TAG, "$className onCreate")
    Holder.INSTANCE = this
    App.shared.testData.add("$this")
  }

  override fun onTerminate() {
    super.onTerminate()
    Log.i(Constants.TAG, "$className onTerminate")
  }


  val sharedPreferences: SharedPreferences get() { return context.getSharedPreferences(packageName, 0) }

  fun testRepeatAction(action: String, sec: Int, interval: Long) {
    Log.i(Constants.TAG, "repeat action $action")
    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, ActionReceiver::class.java)
    intent.action = action
    val alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
    alarmMgr.cancel(alarmIntent)
    alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000 * sec, interval * 1000, alarmIntent)
  }
}