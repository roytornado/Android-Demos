package com.redso.audioprocessing;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MicRecorder {

  public interface VolumeListener {
    /**
     * @param volume 0 - 100
     */
    void onVolumeChanged(int volume);
  }

  private static final String TAG = "MicRecorder";
  private static final boolean debugEnabled = false;

  public MediaRecorder mediaRecorder;
  public Date startTime;
  public Date endTime;
  public int measuringInterval = 300;
  private VolumeListener volumeListener;
  private Timer timer = new Timer();

  public MicRecorder(File file) {
    mediaRecorder = new MediaRecorder();
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    mediaRecorder.setOutputFile(file.getAbsolutePath());
    logDebug("Output file: " + file.getAbsolutePath());
  }

  public void setVolumeListener(VolumeListener volumeListener) {
    this.volumeListener = volumeListener;
  }

  public boolean start() {
    try {
      mediaRecorder.prepare();
      mediaRecorder.start();
      startTime = new Date();
      endTime = null;
      if (volumeListener != null) startTimer();
      return true;
    } catch (IOException e) {
      logError("Can't prepare", e);
      return false;
    }
  }

  public void stop() {
    if (timer != null) {
      timer.cancel();
    }
    volumeListener = null;
    endTime = new Date();
    mediaRecorder.stop();
    mediaRecorder.release();
  }

  private void startTimer() {
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        final int amp = mediaRecorder.getMaxAmplitude();
        final int scale = (int) (Math.min(amp / 32767.0, 1.0) * 100);
        if (volumeListener != null) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              logDebug("onVolumeChanged: " + scale);
              volumeListener.onVolumeChanged(scale);
            }
          });
        }
      }
    }, 0, measuringInterval);
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
