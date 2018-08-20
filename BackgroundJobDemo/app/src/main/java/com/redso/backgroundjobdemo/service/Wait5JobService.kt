package com.redso.backgroundjobdemo.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.redso.backgroundjobdemo.Common
import java.util.*

class Wait5JobService : JobService() {

  val className: String get() = this.javaClass.simpleName

  override fun onStartJob(jobParameters: JobParameters?): Boolean {
    Common.showNotification(baseContext, "$className start")
    val time = Date().time
    while (Date().time - time < 1000 * 5) {
    }
    Common.showNotification(baseContext, "$className finish")
    return true
  }

  override fun onStopJob(p0: JobParameters?): Boolean {
    return true
  }
}