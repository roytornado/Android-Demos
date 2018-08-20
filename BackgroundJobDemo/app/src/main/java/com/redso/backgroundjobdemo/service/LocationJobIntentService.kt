package com.redso.backgroundjobdemo.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import com.redso.backgroundjobdemo.App
import com.redso.backgroundjobdemo.Common

class LocationJobIntentService : BaseJobIntentService() {
  companion object {
    fun enqueueWork(context: Context, intent: Intent) {
      enqueueWork(context, LocationJobIntentService::class.java, 4001, intent)
    }
  }

  override fun onHandleWork(intent: Intent) {
    super.onHandleWork(intent)

    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Common.showNotification(App.context, "[Location] requestLocationUpdates")
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0f, locationListener, Looper.getMainLooper())
    }
  }

  private val locationListener = object : LocationListener {
    override fun onLocationChanged(location: Location?) {
      location?.let {
        Common.showNotification(App.context, "[Location] Changed: ${it.latitude}, ${it.longitude} @ ${it.time}")
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