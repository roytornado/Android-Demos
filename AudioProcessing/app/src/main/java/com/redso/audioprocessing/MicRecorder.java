package com.redso.audioprocessing;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MicRecorder {

  private static final String TAG = "MicRecorder";
  private static final boolean debugEnabled = false;

  public MediaRecorder mediaRecorder;
  public Date startTime;
  public Date endTime;

  public MicRecorder(File file) {
    mediaRecorder = new MediaRecorder();
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    mediaRecorder.setOutputFile(file.getAbsolutePath());
    logDebug("Output file: " + file.getAbsolutePath());
  }

  public boolean start() {
    try {
      mediaRecorder.prepare();
      mediaRecorder.start();
      startTime = new Date();
      endTime = null;
      return true;
    } catch (IOException e) {
      logError("Can't prepare", e);
      return false;
    }
  }

  public void stop() {
    endTime = new Date();
    mediaRecorder.stop();
    mediaRecorder.release();
  }

  public long getDurationInMilliseconds() {
    if (endTime != null) {
      return endTime.getTime() - startTime.getTime();
    }
    return new Date().getTime() - startTime.getTime();
  }

  private void logDebug(String message) {
    if (debugEnabled) Log.d(TAG, message);
  }

  private void logError(String message, Throwable throwable) {
    Log.e(TAG, message, throwable);
  }
}
