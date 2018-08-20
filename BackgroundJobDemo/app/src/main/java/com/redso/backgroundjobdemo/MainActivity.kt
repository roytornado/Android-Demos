package com.redso.backgroundjobdemo

import android.Manifest
import android.app.*
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.redso.backgroundjobdemo.service.ActionReceiver
import com.redso.backgroundjobdemo.service.LocationForegroundService
import com.redso.backgroundjobdemo.service.LocationJobIntentService
import com.redso.backgroundjobdemo.service.Wait5JobService

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    createChannel()
    Dexter.withActivity(this)
      .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      .withListener(permissionListener)
      .check()
  }

  val permissionListener = object : PermissionListener {
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
    }
  }

  fun testBackgroundServiceLimitation(v: View) {
    testAction("testBackgroundServiceLimitation", 3)
  }

  fun testJobIntentService(v: View) {
    //testRepeatAction("testJobIntentService", 3, 10)
    testJobScheduler(ComponentName(this, Wait5JobService::class.java))
  }

  fun testNetworkCallJobIntentService(v: View) {
    testAction("testNetworkCallJobIntentService", 3)
  }

  fun testStepCounterJobIntentService(v: View) {
    testRepeatAction("testStepCounterJobIntentService", 3, 60 * 5)
  }

  fun testLocationJobIntentService(v: View) {
    //testAction("testLocationJobIntentService", 3)
    LocationJobIntentService.enqueueWork(this, Intent())
    finish()
  }

  fun testLocationForegroundService(v: View) {
    startService(Intent(this, LocationForegroundService::class.java))
  }

  fun testJobScheduler(serviceComponent: ComponentName) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Log.i(Constants.TAG, "testJobScheduler $serviceComponent getMinPeriodMillis ${JobInfo.getMinPeriodMillis()}")
      finish()
      val builder = JobInfo.Builder(700, serviceComponent)
      //builder.setMinimumLatency(2000)
      builder.setPeriodic(JobInfo.getMinPeriodMillis())
      (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
    }
  }

  private fun testAction(action: String, sec: Int) {
    finish()
    Log.i(Constants.TAG, "action $action")
    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, ActionReceiver::class.java)
    intent.action = action
    val alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
    alarmMgr.cancel(alarmIntent)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      alarmMgr.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000 * sec, alarmIntent)
    } else {
      alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000 * sec, alarmIntent)
    }
  }

  private fun testRepeatAction(action: String, sec: Int, interval: Long) {
    finish()
    App.shared.testRepeatAction(action, sec, interval)
  }

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val context = this.applicationContext
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val importance = NotificationManager.IMPORTANCE_HIGH
      val notificationChannel = NotificationChannel(Constants.NotificationChannelID, "Background Service Demo Notification Channel", importance)
      notificationChannel.enableVibration(true)
      notificationChannel.setShowBadge(true)
      notificationChannel.enableLights(true)
      notificationChannel.lightColor = Color.parseColor("#e8334a")
      notificationChannel.description = "NotificationChannel Desc"
      notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
      notificationManager.createNotificationChannel(notificationChannel)
    }

  }

}
