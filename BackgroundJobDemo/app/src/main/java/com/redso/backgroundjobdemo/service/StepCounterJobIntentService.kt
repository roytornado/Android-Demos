package com.redso.backgroundjobdemo.service

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.redso.backgroundjobdemo.App
import com.redso.backgroundjobdemo.Common
import com.redso.backgroundjobdemo.formattedDateString
import com.redso.backgroundjobdemo.toDate
import java.util.*

class StepCounterJobIntentService : BaseJobIntentService() {
  companion object {
    fun enqueueWork(context: Context, intent: Intent) {
      enqueueWork(context, StepCounterJobIntentService::class.java, 4001, intent)
    }

    var minPeriodInMinutes = 30

    var lastRecordTimePref = "lastRecordTimePref"
    var lastStepCountPref = "lastStepCountPref"
    var lastCallbackCountPref = "lastCallbackCountPref"

    var registered = false
    var sensorListener = object : SensorEventListener {
      override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.values?.let {
          val value = it.first().toInt()
          val now = Date().time
          val sharedPreferences = App.shared.sharedPreferences

          if (!sharedPreferences.contains(lastRecordTimePref)) {
            sharedPreferences.edit().putLong(lastRecordTimePref, now).apply()
            sharedPreferences.edit().putInt(lastStepCountPref, value).apply()
            sharedPreferences.edit().putInt(lastCallbackCountPref, 0).apply()
            Common.showNotification(App.context, "[StepCounter] Init: ${value} @ ${now.toDate.formattedDateString}")
          } else {
            val lastRecordTime = sharedPreferences.getLong(lastRecordTimePref, 0)

            if (now - lastRecordTime > 1000 * 60 * minPeriodInMinutes) {
              val stepsInPeriod = value - sharedPreferences.getInt(lastStepCountPref, 0)
              val lastCallbackCount = sharedPreferences.getInt(lastCallbackCountPref, 0)
              Common.showNotification(App.context, "[StepCounter] ${stepsInPeriod} from ${lastRecordTime.toDate.formattedDateString} to ${now.toDate.formattedDateString} with $lastCallbackCount updates")

              sharedPreferences.edit().putLong(lastRecordTimePref, now).apply()
              sharedPreferences.edit().putInt(lastStepCountPref, value).apply()
              sharedPreferences.edit().putInt(lastCallbackCountPref, 0).apply()
            } else {
              var lastCallbackCount = sharedPreferences.getInt(lastCallbackCountPref, 0)
              lastCallbackCount += 1
              sharedPreferences.edit().putInt(lastCallbackCountPref, lastCallbackCount).apply()
            }
          }
        }
      }

      override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
      }
    }

    fun resetData() {
      val sharedPreferences = App.shared.sharedPreferences
      sharedPreferences.edit().remove(lastRecordTimePref).apply()
      sharedPreferences.edit().remove(lastStepCountPref).apply()
      sharedPreferences.edit().remove(lastCallbackCountPref).apply()
    }
  }

  override fun onHandleWork(intent: Intent) {
    super.onHandleWork(intent)
    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    if (!registered) {
      //sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
      registered = true
    }
    sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
  }
}