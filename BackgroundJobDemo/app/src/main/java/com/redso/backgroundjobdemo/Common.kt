package com.redso.backgroundjobdemo

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object Common {
  fun showNotification(context: Context, message: String) {
    Log.i(Constants.TAG, "showNotification $message")
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val df = SimpleDateFormat.getDateTimeInstance()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val mNotification = Notification.Builder(context, Constants.NotificationChannelID)
        .setAutoCancel(true)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle("Noti: ${df.format(Date())}")
        .setStyle(Notification.BigTextStyle()
          .bigText(message))
        .setContentText(message).build()


      notificationManager.notify(Date().time.toInt(), mNotification)
    } else {
      val mNotification = Notification.Builder(context)
        .setAutoCancel(true)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle("Noti: ${df.format(Date())}")
        .setStyle(Notification.BigTextStyle()
          .bigText(message))
        .setContentText(message).build()


      notificationManager.notify(Date().time.toInt(), mNotification)
    }
  }
}

val Date.formattedDateString: String
  get() {
    val df = SimpleDateFormat.getDateTimeInstance()
    return df.format(this)
  }

val Long.toDate: Date
  get() = Date(this)