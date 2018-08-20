package com.redso.backgroundjobdemo.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import com.redso.backgroundjobdemo.App
import com.redso.backgroundjobdemo.Common
import com.redso.backgroundjobdemo.Constants
import com.redso.backgroundjobdemo.R

class LocationForegroundService : Service() {

  override fun onBind(intent: Intent?): IBinder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val builder = createNotificationCompatBuilder(this).apply {
      setContentText("Location Tracking")
      setSmallIcon(R.drawable.ic_launcher_background)
    }
    startForeground(500, builder.build())

    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Common.showNotification(App.context, "[LocationForeground] requestLocationUpdates")
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 0f, locationListener)
    }
    return super.onStartCommand(intent, flags, startId)
  }

  fun createNotificationCompatBuilder(context: Context): NotificationCompat.Builder {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return NotificationCompat.Builder(context, Constants.NotificationChannelID)
    } else {
      return NotificationCompat.Builder(context)
    }
  }

  private val locationListener = object : LocationListener {
    override fun onLocationChanged(location: Location?) {
      location?.let {
        Common.showNotification(App.context, "[LocationForeground] Changed: ${it.latitude}, ${it.longitude} @ ${it.time}")
      }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }
  }

}